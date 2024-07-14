module "ecr-repository" {
  source  = "terraform-aws-modules/ecr/aws"
  version = "~> 2.2"

  repository_force_delete = true
  repository_name         = var.ecr_repository_name
  repository_lifecycle_policy = jsonencode({
    rules = [{
      action       = { type = "expire" }
      description  = "Delete all images except the latest [countNumber] images"
      rulePriority = 1
      selection = {
        countNumber = var.ecr_image_count
        countType   = "imageCountMoreThan"
        tagStatus   = "any"
      }
    }]
  })
}

# build the container image locally
resource "docker_image" "build-docker-image" {
  name = format("%v:%v", module.ecr-repository.repository_url, var.docker_artifact_version)
  # will not remove if the image has been pulled into Docker locally and is being used
  force_remove = true
  build {
    context    = var.docker_file_context
    dockerfile = var.docker_file_name
  }
  # only rebuild if the code has changed
  triggers = {
    dir_sha1 = sha1(join("", [for f in fileset(path.module, "src/*") : filesha1(f)]))
  }
}

# push the container image to ECR
resource "docker_registry_image" "push-docker-image" {
  keep_remotely = true # Do not delete old images when a new image is pushed
  name          = docker_image.build-docker-image.name
  depends_on = [docker_image.build-docker-image]
}

# this resource does NOT get destroyed if removed
resource "aws_default_vpc" "default_vpc" {}

resource "aws_alb_target_group" "alb-target-group" {
  name             = var.alb_target_group_name
  target_type      = "ip"
  protocol_version = "HTTP1"
  protocol         = "HTTP"
  port             = var.container_port
  vpc_id           = aws_default_vpc.default_vpc.id
  health_check {
    protocol            = "HTTP"
    interval            = var.health_check_interval
    path                = var.health_check_path
    port                = var.container_port
    unhealthy_threshold = 3
    healthy_threshold   = var.health_check_threshold
  }
}

resource "aws_alb_listener_rule" "service_routing_rule" {
  listener_arn = var.alb_https_listener_arn
  priority = var.alb_listener_rule_priority
  condition {
    path_pattern {
      values = ["${var.ecs_context_path}/*"]
    }
  }
  action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.alb-target-group.arn
  }
  tags = {
    Name = var.ecs_cluster_name
  }
}

module "ecs" {
  source  = "terraform-aws-modules/ecs/aws"
  version = "~> 5.11.2"

  cluster_name = var.ecs_cluster_name

  # * Allocate 20% capacity to FARGATE and then split
  # * the remaining 80% capacity 50/50 between FARGATE
  # * and FARGATE_SPOT.
  fargate_capacity_providers = {
    FARGATE = {
      default_capacity_provider_strategy = {
        base   = 20
        weight = 50
      }
    }
    FARGATE_SPOT = {
      default_capacity_provider_strategy = {
        weight = 50
      }
    }
  }
}

resource "aws_iam_role" "task-execution-role" {
  name = var.ecs_execution_role
  assume_role_policy = data.aws_iam_policy_document.assume-role-document.json
}

resource "aws_iam_role_policy_attachment" "execution-role-policy" {
  policy_arn  = var.ecs_service_role_policy
  role = aws_iam_role.task-execution-role.name
}

resource "aws_cloudwatch_log_group" "service_log_group" {
  name = "/aws/ecs/${var.ecs_cluster_name}/${var.service_name}"
}

resource "aws_ecs_task_definition" "task" {
  family = "family-of-${var.ecs_cluster_name}-tasks"
  cpu = var.cpu
  memory = var.memory
  network_mode = "awsvpc"
  requires_compatibilities = [var.fargate]
  execution_role_arn = aws_iam_role.task-execution-role.arn
  container_definitions = jsonencode([{
    essential = true,
    image = docker_image.build-docker-image.name,
    name = var.container_name,
    logConfiguration = {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": aws_cloudwatch_log_group.service_log_group.name,
        "awslogs-region": var.log_region,
        "awslogs-stream-prefix": "tasks"
      }
    }
    portMappings = [
      {
        containerPort = var.container_port
      }
    ],
    healthCheck = {
      "command": ["CMD-SHELL", "curl -f http://localhost:${var.container_port}${var.health_check_path} || exit 1"],
      # defaults must be specified else terraform thinks the resource has changed
      "interval": var.health_check_interval,
      "retries": var.health_check_retries,
      "timeout": var.health_check_interval
    }
    environment: [
      {
        "name": "spring.profiles.active",
        "value": var.spring_profile
      }]
  }])
  depends_on = [
    docker_registry_image.push-docker-image,
    module.ecs,
    aws_iam_role.task-execution-role,
  ]
}

resource "aws_ecs_service" "service" {
  cluster = module.ecs.cluster_id
  desired_count = 1
  launch_type = var.fargate
  name = var.service_name
  task_definition = aws_ecs_task_definition.task.arn

  lifecycle {
    ignore_changes = [desired_count] # Allow auto-scaling.
  }

  load_balancer {
    container_name = var.container_name
    container_port = var.container_port
    target_group_arn = aws_alb_target_group.alb-target-group.arn
  }

  network_configuration {
    security_groups = ["${aws_security_group.service_security_group.id}"]
    subnets = var.ecs_private_subnets
  }

  depends_on = [
    aws_alb_target_group.alb-target-group,
    aws_security_group.service_security_group,
    aws_ecs_task_definition.task
  ]
}

resource "aws_security_group" "service_security_group" {
  name = "rn-secgrp-${var.ecs_cluster_name}-${var.service_name}"
  ingress {
    from_port = var.container_port
    to_port = var.container_port
    protocol = "TCP"
    security_groups = ["${var.www_security_group}"]
  }
  egress {
    from_port = 0
    to_port = 65535
    protocol = "TCP"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

