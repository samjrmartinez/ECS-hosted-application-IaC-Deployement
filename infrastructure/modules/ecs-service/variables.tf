variable "service_name" {
  type = string
  description = "Name of the ECS Service"
}

variable "alb_arn" {
  type = string
  description = "ARN of the load balancer to attach listener rules and target groups"
}

variable "alb_https_listener_arn" {
  type = string
  description = "ARN of the https listener in the load balancer, above"
}

variable "alb_listener_rule_priority" {
  type = number
  description = "Priority of this rule in relation to any others on the same listener"
}

variable "alb_target_group_name" {
  type = string
  description = "Name of the load balancer target group"
}

variable "container_name" {
  type = string
  description = "Name of the ECS Container"
}

variable "container_port" {
  type = number
  description = "Service port number"
}

variable "ecs_log_group_name" {
  type = string
  description = "Name of log group for application logs"
}

variable "ecs_cluster_name" {
  type = string
  description = "incentive-stage"
}

variable "ecs_context_path" {
  type = string
  description = "context-path value set in the service application.yml file"
}

variable "ecs_private_subnets" {
  type = list(string)
  default = ["subnet-0c0fe8fb88f7c3958", "subnet-0a3f572fb9547a865"]
  description = "List of (2) private subnets to deploy ECS tasks and services to"
}

variable "alb_public_subnets" {
  type = list(string)
  default = ["subnet-29adf74c", "subnet-900be8ca"]
  description = "List all the public subnets for the VPC"
}

variable "ecs_execution_role" {
  type = string
  description = "task execution role"
}

variable "ecs_service_role_policy" {
  type = string
  default = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

variable "ecr_image_count" {
  type = number
  description = "Number of newest images to keep in the repository"
}

variable "ecr_repository_name" {
  type = string
  description = "Application Namespace to use for ECR repository"
}

variable "docker_artifact_version" {
  type = string
  description = "Version/build number for this application"
}

variable "docker_file_name" {
  type = string
  description = "name of the Dockerfile to execute for image generation"
}

variable "docker_file_context" {
  type = string
  default = "./../"
  description = "Relative path to the docker file from the terraform run location"
}

variable "fargate" {
  type = string
  default = "FARGATE"
  description = "enumerated value for fargate resources"
}

variable "cpu" {
  type = number
  description = "256=0.25vCPU, 512=0.50vCPU, 1024=1vCPU, 2048=2vCPU, 4096=4vCPU, 8192=8vCPU"
}

variable "memory" {
  type = number
  description = "Amount of memory to apply for each call, in MB"
}

variable "custom_certificate_arn" {
  type = string
  default = "arn:aws:acm:[region]:[accountId]:certificate/[certificateId]"
}

variable "load_balancer_ssl_policy" {
  type = string
  default = "ELBSecurityPolicy-TLS13-1-2-2021-06"
}

variable "health_check_path" {
  type = string
  description = "Path to endpoint for health check calls"
}

variable "health_check_interval"{
  type = number
  validation {
    condition = can(regex("^(?:[5-9]{1}|[1-9][0-9]|[12]\\d{2}|300)$", var.health_check_interval))
    error_message = "Valid health check interval values are between 5 and 300"
  }
  description = "Amount of time between health check calls"
}

variable "health_check_retries" {
  type = number
  description = "Number of health check retries before draining"
}

variable "health_check_threshold" {
  type = number
  validation {
    condition = can(regex("^([2-9]|10){1}$", var.health_check_threshold))
    error_message = "Valid health check threshold values are between 2 and 10"
  }
  description = "Number of consecutive successful health checks constitute a healthy condition"
}

variable "spring_profile" {
  type = string
  description = "Name of the Spring profile the application should run under"
}

variable "log_region" {
  type = string
  default = "us-east-1"
  description = "Region for log placement"
}

variable "microservice_security_groups" {
  type = list(string)
  description = "List of security groups to use for this microservice"
}

variable "www_security_group" {
  type = string
  description = "Group Id of Security group containing -www- in the name. There is one for STAGE and one for PROD"
}