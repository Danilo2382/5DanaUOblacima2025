variable "ec2_instance_type" {
  type        = string
  description = "EC2 Resource Type"
}

variable "app_name" {
  type        = string
  description = "App Name"
}

variable "private_subnet_id" {
  type        = string
  description = "Private Subnet ID"
}

variable "ec2_sg_id" {
  type        = string
  description = "EC2 Security Group ID"
}
