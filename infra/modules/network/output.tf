output "vpc_id" {
  value = aws_vpc.main_vpc.id
}

output "db_subnet_group_name" {
  value = aws_db_subnet_group.database_subnet_group.name
}

output "private_subnet_id" {
  value = aws_subnet.private_subnet.id
}
