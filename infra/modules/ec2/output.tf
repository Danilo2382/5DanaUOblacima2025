output "ec2_instance_private_ip" {
  value = aws_instance.springboot_ec2.private_ip
}
