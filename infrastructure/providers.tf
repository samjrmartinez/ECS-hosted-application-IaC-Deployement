terraform {

  backend "s3" {
    bucket         = "terraform-custom-state"
    key            = "tf-infra/terraform.tfstate"
    region         = "[region]"
    dynamodb_table = "terraform_locks"
    encrypt        = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = "[region]"
}