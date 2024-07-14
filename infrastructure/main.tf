# give docker permission to pusher Docker images to AWS
provider "docker" {
  registry_auth {
    address  = local.ecr_address
    password = data.aws_ecr_authorization_token.this.password
    username = data.aws_ecr_authorization_token.this.user_name
  }
}

module "tf-state" {
  source                 = "./modules/tf-state"
  save_state_bucket_name = local.bucket_name
  dynamo_lock_table_name = local.table_name
}

# description: "Create and configure PostgreSQL serverless database"
module "custom-database" {
  source = "./modules/rds-database"

  database_parameter_group_name = local.database_parameter_group_name
  database_engine               = local.database_engine
  database_engine_version       = local.database_engine_version
  database_availability_zone    = local.database_availability_zone
  database_engine_mode          = local.database_engine_mode
  database_instance_class       = local.database_instance_class
  database_username             = local.database_username
  database_password             = local.database_password
  database_name                 = local.database_name
  database_port                 = local.database_port
  database_cluster_name         = local.database_cluster_name
  database_instance_name        = local.database_instance_name
  database_final_snapshot_name  = local.database_final_snapshot_name
  database_security_group_ids   = local.database_security_group_ids
  database_maximum_capacity     = local.database_maximum_capacity
}

# description: "Deploy ECS service and task(s) using the image from above"
module "custom-service" {
  source = "./modules/ecs-service"

  service_name                 = local.service_name
  alb_arn                      = local.alb_arn
  alb_https_listener_arn       = local.alb_https_listener_arn
  alb_listener_rule_priority   = local.alb_listener_rule_priority
  alb_target_group_name        = local.alb_target_group_name
  container_name               = local.container_name
  container_port               = local.container_port
  ecs_log_group_name           = local.ecs_log_group_name
  ecs_cluster_name             = local.ecs_cluster_name
  ecs_context_path             = local.ecs_context_path
  ecs_execution_role           = local.ecs_execution_role
  ecr_image_count              = local.ecr_image_count
  ecr_repository_name          = local.ecr_repository_name
  docker_artifact_version      = local.docker_artifact_version
  docker_file_name             = local.docker_file_name
  cpu                          = local.cpu
  memory                       = local.memory
  health_check_interval        = local.health_check_interval
  health_check_path            = local.health_check_path
  health_check_retries         = local.health_check_retries
  health_check_threshold       = local.health_check_threshold
  spring_profile               = local.spring_profile
  microservice_security_groups = local.microservice_security_groups
  www_security_group           = local.www_security_group
}