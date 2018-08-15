# Environment variables

## Mandatory environment variables

build_user: User for connecting to newbuild

build_password: Password for connecting to newbuild

## Optional environment variables

build_version: Version to look for on new build (take the latest one by default) (ie: V7.0.1SNAPSHOT)

build_version_date: Specific version to choose on new build (take the latest one by default) (ie: V7.0.1SNAPSHOT_20180402_1933)

## Adaptation to local environment

### With docker only

Appropriate images will be used by the script. No need to do anything

### With ansible and terraform installed

Simply set those environment variables use by the script
```
export ANSIBLE_CMD="ansible-playbook"
export TERRAFORM_CMD="terraform"
```

# Create an on-prem EC2 instance

You can pass as parameters the type of machine you want to create in the shell

For Suse SLES 12 SP3
```
./main.sh "create" "suse"
```
For windows 2016
```
./main.sh "create" "windows2016"
```

If you are outside the target vpc don't forget to add a bastion
```
./main.sh "create" "suse" ec2-52-20-67-78.compute-1.amazonaws.com"
```

# Destroy an on-prem EC2 instance

You can pass as parameters the type of machine you want to create in the shell

For Suse SLES 12 SP3
```
./main.sh "destroy" "suse"
```
For windows 2016
```
./main.sh "destroy" "windows2016"
```

# Connecting manually to an instance

## Linux instance
You can see what's happening by connecting to the instance with SSH via the bastion and then use the ec2-user.
For exemple with the command :
```
ssh -A -t [bastion] ssh -A ec2-user@[instance ip]
```
/!\ You must have the same ssh key in your .ssh directory that the one used to create the instance with terraform

## Windows instance
You can see what's happening by connecting to the instance with RDP by creating a tunnel for the port 3389 via the bastion.
For exemple with the command :
```
ssh -L3389:[instance ip]:3389 -t [bastion]
```
Then any rdp client will do if you connect to localhost and port 3389.
The login have been set by windows_user_data.txt file.
