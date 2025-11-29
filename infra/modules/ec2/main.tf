data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_instance" "springboot_ec2" {
  ami             = data.aws_ami.ubuntu.id
  subnet_id       = var.private_subnet_id
  instance_type   = var.ec2_instance_type
  security_groups = [var.ec2_sg_id]

  key_name = "keypair"

  # user_data = <<-EOF
  #             #!/bin/bash
  #             cd /home/ec2-user
  #             aws s3 cp s3://your-bucket/springboot.jar .
  #             java -jar springboot.jar
  #             EOF
}
