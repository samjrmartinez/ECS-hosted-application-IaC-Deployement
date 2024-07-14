locals {
  service_name = "ecs-custom-service"
  # targeting an existing load balancer
  alb_arn = "arn:aws:elasticloadbalancing:[region]:[accountId]:loadbalancer/app/[existing-load-balancer-name]/[load-balancer-id]"
  # targeting the https (443) listener in an existing load balancer
  alb_https_listener_arn       = "arn:aws:elasticloadbalancing:[region]:[accountId]:listener/app/[existing-load-balancer-name]/[load-balancer-id]/[https-listener-id]"
  alb_listener_rule_priority   = 100
  alb_target_group_name        = "custom-target-group-name"
  ecr_address                  = format("%v.dkr.ecr.%v.amazonaws.com", data.aws_caller_identity.this.account_id, data.aws_region.this.name)
  container_name               = "custome-service-container"
  container_port               = 8096
  bucket_name                  = "terraform-incentives-state"
  table_name                   = "terraform_locks"
  ecs_log_group_name           = "/aws/ecs/custom/service"
  ecs_cluster_name             = "custom-service-cluster"
  ecs_context_path             = "/api/v1/custom" #for alb routing
  ecs_execution_role           = "ecs-iam-[region]-role-[environment]-custom-service"
  ecr_image_count              = 3
  ecr_repository_name          = "com.sammartinez.example.api"
  docker_artifact_version      = "0.0.1"
  docker_file_name             = "Dockerfile-non-prod"
  cpu                          = 256
  memory                       = 512
  health_check_path            = "/api/v1/custom/actuator/health"
  health_check_retries         = 5
  health_check_threshold       = 2
  health_check_interval        = 60
  microservice_security_groups = ["sg-0bad8154f8e673778"]
  spring_profile               = "ecs-non-prod"
  www_security_group           = "sg-0bad8154f8e673778"


  database_parameter_group_name = "default.aurora-postgresql15"
  database_engine               = "aurora-postgresql"
  database_engine_version       = "15.4"
  database_availability_zone    = "us-east-1c"
  database_engine_mode          = "provisioned"
  database_instance_class       = "db.serverless"
  database_username             = "custom"
  database_password             = "password"
  database_name                 = "username"
  database_port                 = 5432
  database_cluster_name         = "ecs-rds-[region]-aurora-postgresql-s-custom"
  database_instance_name        = "ecs-rds-[region]-aurora-postgresql-s-custom-instance-1"
  database_final_snapshot_name  = "ecs-rds-[region]-aurora-postgresql-s-custom-final-snapshot"
  database_security_group_ids   = ["sg-07b63840c37de0fe9"]
  database_maximum_capacity     = 16
}