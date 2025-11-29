resource "aws_vpc" "main_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
}

resource "aws_subnet" "public_subnet" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-central-1a"
}

resource "aws_subnet" "private_subnet" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.2.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-central-1b"
}

# resource "aws_internet_gateway" "main_vpc" {
#   vpc_id = aws_vpc.main_vpc.id
# }

# resource "aws_route_table" "public_route_table" {
#   vpc_id = aws_vpc.main_vpc.id
# }

# resource "aws_route" "internet_access" {
#   route_table_id         = aws_route_table.public_route_table.id
#   destination_cidr_block = "0.0.0.0/0"
#   gateway_id             = aws_internet_gateway.main_vpc.id
# }

# resource "aws_route_table_association" "public_subnet_association" {
#   subnet_id      = aws_subnet.public_subnet.id
#   route_table_id = aws_route_table.public_route_table.id
# }

resource "aws_db_subnet_group" "database_subnet_group" {
  name       = "${var.app_name}-db-subnet-group"
  subnet_ids = [aws_subnet.private_subnet.id]
}
