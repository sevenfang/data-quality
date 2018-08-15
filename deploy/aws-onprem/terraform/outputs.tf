output "suse_private_dns" {
  value = ["${aws_instance.testonprem_instance_suse.private_dns}"]
}

output "windows2016_private_dns" {
  value = ["${aws_instance.testonprem_instance_windows2016.private_dns}"]
}
