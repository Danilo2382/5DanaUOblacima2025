variable "region" {
  type        = string
  description = "The AWS region to deploy resources in."
  default     = "eu-central-1"
}

variable "app_name" {
  type        = string
  description = "App Name"
  default     = "velikamenza"
}

variable "ec2_instance_type" {
  type        = string
  description = "EC2 Resource Type"
  default     = "t3.micro"
}

variable "db_username" {
  type        = string
  description = "Database Username"
  default     = "postgres"
}

variable "db_password" {
  type        = string
  description = "Database Password"
  default     = "postgres"
}

variable "db_name" {
  type        = string
  description = "Database Name"
  default     = "postgres"
}
