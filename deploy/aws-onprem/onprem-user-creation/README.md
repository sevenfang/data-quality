#  Provisioning requested users for load testing on TDS on-premise deployment

# Utility content
This utility has two main scripts:
- `create.sh`: inject users in TAC database, based on the content of `./users` folder content.
- `delete.sh`: delete users from TAC database.

# Script `create.sh`
## Principle
This script will scan the `./users` folder for JSON files.

For each JSON file, an RPC call against TAC MetaServlet will be done to create a user based on the content of the JSON file.

## Script configuration
Edit `./create.sh` file.

Parameters to be set is:
- CONF_FILE: This is a reference to a file contained in `./conf` directory.
> CONF_FILE="tal-qa151.conf"

This file contains the access configuration to TAC on premise deployment.

*TIP:* You can add your own targeted cloud stack access configuration file into the `./conf` folder.

This file contains five parameters:
- TAC_URL: FQDN of targeted TAC hosting platform
- TAC_PORT: Targeted TAC port
- TAC_SERVICE: Targeted TAC service name
- TAC_LOGIN: TAC admin login
- TAC_PASSWORD: TAC admin password

If you access TAC with its default configuration @ http://localhost:8080/org.talend.administrator

Then your configuration file should be:

> TAC_URL="http://localhost"
>
>TAC_PORT="8080"
>
> TAC_SERVICE="org.talend.administrator"
>
> TAC_LOGIN="security@compagny.com"
>
> TAC_PASSWORD="admin"


## Usage
1. Check that `./user` folder contains requested JSON file
2. Set `CONF_FILE` in script parameter
1. Execute the script:
> `you@localhost#> ./create.sh`

&nbsp;

**NOTE:**
1. You have an helper utility in `./user-json-files-generation` to generate requested JSON file(s).
1. JSON file specifications can be retrieved from TAC MetaServlet API doc, Command: createUser

# Script `delete.sh`

## Getting help

`you@localhost#> ./delete.sh --help`

> ./delete.sh -a | -f
>
> -a : Delete all TDS users
>
> -f : Delete users which JSON file is located in ./user-to-delete

## Script configuration
Edit `./delete.sh` file.

Parameters to be set is:
- CONF_FILE: This is a reference to a file contained in `./conf` directory.
> CONF_FILE="tal-qa151.conf"

This file contains the access configuration to TAC on premise deployment.

*TIP:* You can add your own targeted cloud stack access configuration file into the `./conf` folder.

This file contains five parameters:
- TAC_URL: FQDN of targeted TAC hosting platform
- TAC_PORT: Targeted TAC port
- TAC_SERVICE: Targeted TAC service name
- TAC_LOGIN: TAC admin login
- TAC_PASSWORD: TAC admin password

If you access TAC with its default configuration @ http://localhost:8080/org.talend.administrator

Then your configuration file should be:

> TAC_URL="http://localhost"
>
>TAC_PORT="8080"
>
> TAC_SERVICE="org.talend.administrator"
>
> TAC_LOGIN="security@compagny.com"
>
> TAC_PASSWORD="admin"

## Usage
### With *"-f"* option
Command:

> `./delelte.sh -f`

&nbsp;

Script will scan `./user-to-delete` folder for JSON files.

For each JSON file, it will extract the `".userLogin"` entry (that should be user's email address), and delete this user in TAC.

&nbsp;

Example of a JSON file content from `./user-to-delete` folder:

> `{"userLogin": "me@here.com"}`

**NOTE:** Every JSON file containing a *"userLogin"* entry at its top level is compatible with this script

### With *"-a"* option
Command:

> `./delelte.sh -a`

List of users to be deleted is directly retrieved from TAC.

All **Talend Data Stewardship** users will be retrieved from TAC and then deleted.

