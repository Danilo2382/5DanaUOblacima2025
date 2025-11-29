variable "db_name" {
  type        = string
  description = "Database Name"
}

variable "db_username" {
  type        = string
  description = "Database Username"
}

variable "db_password" {
  type        = string
  description = "Database Password"
}

variable "db_subnet_group_name" {
  type        = string
  description = "Database Subnet Group Name"
}

variable "rds_sg_id" {
  type        = string
  description = "RDS Security Group ID"
}
