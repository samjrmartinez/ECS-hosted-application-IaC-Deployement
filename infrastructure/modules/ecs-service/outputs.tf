output "image_name" {
  value = docker_image.build-docker-image.name
}

output "repository_url" {
  value = module.ecr-repository.repository_url
}