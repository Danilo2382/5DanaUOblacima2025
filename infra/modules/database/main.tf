resource "aws_db_instance" "postgresdb" {
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "13.4"
  instance_class         = "db.t3.micro"
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  parameter_group_name   = "default.postgres13"
  skip_final_snapshot    = true
  vpc_security_group_ids = [var.rds_sg_id]
  db_subnet_group_name   = var.db_subnet_group_name
}
