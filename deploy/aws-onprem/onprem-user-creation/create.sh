#!/bin/bash

# exit on any error
set -e

#############################################
# User parameters: edit to your convenience #
#############################################

test -z "${CONF_FILE}" && CONF_FILE="tal-qa92.conf"

##################################
# Script parameter : DO NOT EDIT #
##################################
EXEC_DIR=$(dirname "$0")
USER_DIR="${EXEC_DIR}/users"

# Load configuration file
CONF_PATH="${EXEC_DIR}/conf/${CONF_FILE}"
source ${CONF_PATH}

###################################

function usage() {
    echo "$0"
    echo "   - Will create the TAC users base on the content of the \"${USER_DIR}\" directory"
}

if [ -n "$(ls -A ${USER_DIR}/*.json 2>/dev/null)" ]
then

    for USER_JSON in ${USER_DIR}/*.json; do

        IDENTIFIED_PAYLOAD=$(sed -e "s|{{TAC_LOGIN}}|"${TAC_LOGIN}"|g" -e "s|{{TAC_PASSWORD}}|"${TAC_PASSWORD}"|g" ${USER_JSON})

        BASE64_PAYLOAD="$(echo ${IDENTIFIED_PAYLOAD} | base64 -w 0)"
        echo "Creating user based on file => ${USER_JSON}"
        echo "*******************************************"
        echo "http://${TAC_URL}:${TAC_PORT}/${TAC_SERVICE}/metaServlet?${BASE64_PAYLOAD}"
        echo ""
        curl http://${TAC_URL}:${TAC_PORT}/${TAC_SERVICE}/metaServlet?${BASE64_PAYLOAD}
        echo ""
        echo ""
    done
else
    echo "No file *.json detected in ${USER_DIR} directory"
    echo ""
    echo "To help you generate those JSON files, you can find help here"
    echo "https://github.com/Talend/data-stewardship/tree/master/data-stewardship-qa/data-stewardship-system-tests/data-stewardship-load-tests/tooling/onprem-user-creation/user-json-files-generation"
    echo ""
    usage
fi