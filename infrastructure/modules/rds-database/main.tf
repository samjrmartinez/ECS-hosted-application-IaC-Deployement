# Aurora PostgreSQL Billing DB Writer Instance
resource "aws_rds_cluster_instance" "custom-writer-instance" {
  identifier          = var.database_instance_name
  cluster_identifier  = aws_rds_cluster.custom_database_cluster.id
  instance_class      = var.database_instance_class
  publicly_accessible = true
  engine              = var.database_engine
  engine_version      = var.database_engine_version
  availability_zone   = var.database_availability_zone

  lifecycle {
    prevent_destroy = false
  }
}

#Aurora PostgreSQL custom DB Cluster
resource "aws_rds_cluster" "custom_database_cluster" {
  cluster_identifier              = var.database_cluster_name
  engine                          = var.database_engine
  engine_mode                     = var.database_engine_mode
  engine_version                  = var.database_engine_version
  db_cluster_parameter_group_name = var.database_parameter_group_name
  db_subnet_group_name            = var.database_subnet_group_name
  deletion_protection             = true

  database_name          = var.database_name
  port                   = var.database_port
  vpc_security_group_ids = var.database_security_group_ids
  network_type           = "IPV4"
  master_username        = var.database_username
  master_password        = var.database_password

  storage_encrypted         = true
  backup_retention_period   = 1
  preferred_backup_window   = "08:24-09:00"
  copy_tags_to_snapshot     = true
  skip_final_snapshot       = false
  final_snapshot_identifier = var.database_final_snapshot_name

  serverlessv2_scaling_configuration {
    min_capacity = var.database_minimum_capacity
    max_capacity = var.database_maximum_capacity
  }

  lifecycle {
    prevent_destroy = false
  }
}