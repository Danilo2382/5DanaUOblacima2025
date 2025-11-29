terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_sns_topic" "email_notifications_topic" {
  name = "AppEmailNotifications"
}

resource "aws_sns_topic_subscription" "email_subscription" {
  endpoint  = var.subscription_email
  protocol  = "email"
  topic_arn = aws_sns_topic.email_notifications_topic.arn
}

data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    effect = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["ec2.amazonaws.com"]
      type = "Service"
    }
  }
}

resource "aws_iam_role" "app_publisher_role" {
  name               = "EC2_SNS_Publisher_Role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
}

data "aws_iam_policy_document" "sns_publish_policy" {
  statement {
    effect    = "Allow"
    actions   = ["sns:Publish"]
    resources = [aws_sns_topic.email_notifications_topic.arn]
  }
}

resource "aws_iam_policy" "sns_publish_policy" {
  name   = "SNSPublishPolicy-${aws_sns_topic.email_notifications_topic.name}"
  policy = data.aws_iam_policy_document.sns_publish_policy.json
}

resource "aws_iam_role_policy_attachment" "sns_publish_attach" {
  role       = aws_iam_role.app_publisher_role.name
  policy_arn = aws_iam_policy.sns_publish_policy.arn
}

resource "aws_iam_instance_profile" "app_instance_profile" {
  name = "EC2_SNS_Instance_Profile"
  role = aws_iam_role.app_publisher_role.name
}

output "sns_topic_arn" {
  description = "ARN SNS Topics for Spring Boot configuration"
  value       = aws_sns_topic.email_notifications_topic.arn
}

output "instance_profile_name" {
  description = "Name of IAM Instance Profile for EC2 instance"
  value       = aws_iam_instance_profile.app_instance_profile.name
}