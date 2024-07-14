output "rds_database_endpoint" {
  value = aws_rds_cluster.custom_database_cluster.endpoint
}