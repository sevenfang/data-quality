#!/bin/bash

# exit on any error
set -e

#############################################
# User parameters: edit to your convenience #
#############################################

CONF_FILE="tal-qa151.conf"

##################################
# Script parameter : DO NOT EDIT #
##################################
EXEC_DIR=$(dirname "$0")
BACKUP_DIR="${EXEC_DIR}/backup"
TMP_ALL_TDS_USER_FILE="all_tds_users.json"
OUTPUT_DIR="${EXEC_DIR}/user-to-delete"


DELETED_USER_DIR="${EXEC_DIR}/deleted-users"
if [[ ! -d ${DELETED_USER_DIR} ]]
then
    mkdir -p ${DELETED_USER_DIR}
fi

TMP_DIR="${EXEC_DIR}/tmp"
if [[ ! -d ${TMP_DIR} ]]
then
    mkdir -p ${TMP_DIR}
else
    rm -rf ${TMP_DIR}/*
fi

TEMPLATE_DIR="${EXEC_DIR}/template"
TEMPLATE_FILE="${TEMPLATE_DIR}/delete-user.json.template"

TAC_CMD_ALL_TDS_USERS="${TEMPLATE_DIR}/tac-cmd-all-tds-users.json"

# Load configuration file
CONF_PATH="${EXEC_DIR}/conf/${CONF_FILE}"
source ${CONF_PATH}


###################################
function usage() {
    echo "$0 -a | -f"
    echo "-a : Delete all TDS users"
    echo "-f : Delete users which JSON file is located in ${OUTPUT_DIR}"
}

function generateUserToDeleteFromFiles() {

    # Generate JSON payload for user deletion
    for USER_JSON in ${OUTPUT_DIR}/*.json; do

        USER_LOGIN=$(jq -r ".userLogin" ${USER_JSON})

        sed -e "s|{{USER_LOGIN}}|"${USER_LOGIN}"|g" \
            -e "s|{{TAC_LOGIN}}|"${TAC_LOGIN}"|g" \
            -e "s|{{TAC_PASSWORD}}|"${TAC_PASSWORD}"|g" \
            ${TEMPLATE_FILE} >  ${OUTPUT_DIR}/${USER_LOGIN}.json

    done
}

function generateUserToDeleteFromTac() {

    local BASE64_PAYLOAD="$(cat ${TAC_CMD_ALL_TDS_USERS} | base64 -w 0)"
    curl -s http://${TAC_URL}:${TAC_PORT}/${TAC_SERVICE}/metaServlet?${BASE64_PAYLOAD} > ${TMP_DIR}/${TMP_ALL_TDS_USER_FILE}

    for USER_LOGIN in $(jq -r ".users[].email" ${TMP_DIR}/${TMP_ALL_TDS_USER_FILE}); do

        sed -e "s|{{USER_LOGIN}}|"${USER_LOGIN}"|g" \
            -e "s|{{TAC_LOGIN}}|"${TAC_LOGIN}"|g" \
            -e "s|{{TAC_PASSWORD}}|"${TAC_PASSWORD}"|g" \
            ${TEMPLATE_FILE} >  ${OUTPUT_DIR}/${USER_LOGIN}.json
    done
}

if [[ $# -ne 1 ]]
then
    usage
    exit 1
fi

case $1 in
    --all|-a)
        generateUserToDeleteFromTac;;
    --file|-f)
        generateUserToDeleteFromFiles;;
    --help|-h)
        usage
        exit 0
        ;;
    *)
        echo ""
        echo "!!! Unknown option \"$1\" !!!"
        usage
        exit 1
        ;;
esac

# Execute deletion
for JSON_PAYLOAD in ${OUTPUT_DIR}/*.json; do
    BASE64_PAYLOAD="$(cat ${JSON_PAYLOAD} | base64 -w 0)"
    echo "Deleting user " $(jq -r ".userLogin" ${JSON_PAYLOAD})
    echo "**********"
    curl http://${TAC_URL}:${TAC_PORT}/${TAC_SERVICE}/metaServlet?${BASE64_PAYLOAD}
    mv ${JSON_PAYLOAD} ${DELETED_USER_DIR}

    echo ""
done