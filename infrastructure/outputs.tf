output "custom_docker_image_name" {
  value = module.custom-service.image_name
}

output "custom_repository_url" {
  value = module.custom-service.repository_url
}

output "custom_database_endpoint" {
  value = module.custom-database.rds_database_endpoint
}