terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.92"
    }
  }

  required_version = ">= 1.2"
}

provider "aws" {
  region = var.region
}

module "ec2" {
  source            = "./modules/ec2"
  app_name          = var.app_name
  private_subnet_id = module.network.private_subnet_id
  ec2_sg_id         = module.securitygroups.ec2_sg_id
  ec2_instance_type = var.ec2_instance_type
}

module "apigateway" {
  source                  = "./modules/apigateway"
  app_name                = var.app_name
  ec2_instance_private_ip = module.ec2.ec2_instance_private_ip
}

module "network" {
  source   = "./modules/network"
  app_name = var.app_name
}

module "securitygroups" {
  source = "./modules/securitygroups"
  vpc_id = module.network.vpc_id
}

module "database" {
  source               = "./modules/database"
  rds_sg_id            = module.securitygroups.rds_sg_id
  db_username          = var.db_username
  db_password          = var.db_password
  db_name              = var.db_name
  db_subnet_group_name = module.network.db_subnet_group_name
}
