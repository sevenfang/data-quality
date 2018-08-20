terraform {
  backend "s3" {
    bucket = "us-east-1-priv-devops-talend-com"
    region = "us-east-1"
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_key_pair" "testonprem_key_pair" {
  key_name_prefix = "tdq-testonprem-"
  public_key = "${file("${var.ssh_public_key_path}")}"
}

resource "aws_instance" "testonprem_instance_suse" {
  count = "1"
  ami = "ami-62bda218"
  instance_type = "t2.small"
  subnet_id = "${data.aws_subnet.build_main_subnet.id}"
  vpc_security_group_ids = ["${data.aws_security_group.default_build_main_sg.id}"]
  associate_public_ip_address = "true"
  key_name = "${aws_key_pair.testonprem_key_pair.key_name}"
  root_block_device {
    volume_type = "gp2"
    volume_size = "50"
  }
  tags = {
    Name = "tdq-testonprem-instance-suse",
    Owner = "tdq"
    Environment = "testonprem"
    Service = "standalone-java-node"
    Project = "dataquality"
    Team = "tdq"
    CostCenter = "AWS R&D"
    Status = "temporary"
    Provisioner = "terraform"
    BackedUp = "No"
  }
}

resource "aws_instance" "testonprem_instance_windows2016" {
  count = "1"
  ami = "ami-4176943c"
  instance_type = "t2.small"
  subnet_id = "${data.aws_subnet.build_main_subnet.id}"
  vpc_security_group_ids = ["${data.aws_security_group.default_build_main_sg.id}"]
  associate_public_ip_address = "true"
  user_data = "${file("files/windows_user_data.txt")}"
  root_block_device {
    volume_type = "gp2"
    volume_size = "50"
  }
  tags = {
    Name = "tdq-testonprem-instance-windows2016",
    Owner = "tdq"
    Environment = "testonprem"
    Service = "standalone-java-node"
    Project = "dataquality"
    Team = "tdq"
    CostCenter = "AWS R&D"
    Status = "temporary"
    Provisioner = "terraform"
    BackedUp = "No"
  }
}
