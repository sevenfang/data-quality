data "aws_security_group" "default_build_main_sg" {
  vpc_id = "${data.aws_vpc.build_main_vpc.id}"
  id = "sg-52c4282b" # Default build-main VPC security group
}

data "aws_subnet" "build_main_subnet" {
  vpc_id = "${data.aws_vpc.build_main_vpc.id}"
  id = "subnet-eee63398" # Main subnet in build-main VPC on us-east-1b
}

data "aws_vpc" "build_main_vpc" {
  id = "vpc-5feb703b" # VPC managed by build-main
}

variable "ssh_public_key_path" {
  default = "~/.ssh/id_rsa.pub"
}
