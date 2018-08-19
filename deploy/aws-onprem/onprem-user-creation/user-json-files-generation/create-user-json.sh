#!/bin/bash

# exit on any error
set -e

##################################
# Script parameter - do not edit #
##################################
EXEC_DIR=$(dirname "$0")
TEMPLATE_DIR="${EXEC_DIR}/template"
OUTPUT_DIR="${EXEC_DIR}/output"
if [[ ! -d ${OUTPUT_DIR} ]]
then
    mkdir -p ${OUTPUT_DIR}
fi
BACKUP_DIR="${EXEC_DIR}/backup"

##################################

function usage () {
    echo ""
	echo "Script usage:"
	echo "$0 --campaignOwner idxStart,idxEnd --dataSteward idxStart,idxEnd"
	echo ""
}

function setIndexes() {

    local INDEX_TYPE=$1
	local IN=$2
	local OUT=(${IN//,/ })

	case $INDEX_TYPE in
		co)
			CO_START_IDX=${OUT[0]}
			CO_STOP_IDX=${OUT[1]}
		;;
		ds)
			DS_START_IDX=${OUT[0]}
			DS_STOP_IDX=${OUT[1]}
		;;
		*)
		echo "Unknown index type"
		exit 1
	esac
}

function generateJsonFile () {

    local OUTPUT_FILE_PREFIX=$1
    local TEMPLATE_FILE=$2
    local INDEX=$3

    sed -e "s@{{INDEX}}@"${INDEX}"@g" \
        ${TEMPLATE_FILE} >  ${OUTPUT_DIR}/${OUTPUT_FILE_PREFIX}${INDEX}.json

}

# read command line parameters
while [[ "$#" > 1 ]]
do
	case $1 in
	    --campaignOwner|-co)
			CO_IDX="$2";;
	    --dataSteward|-ds)
			DS_IDX="$2";;
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
  	shift; shift
done

# Set Campaign Owner indexes
if [[ -n ${CO_IDX} ]]
then
	setIndexes "co" ${CO_IDX}
else
    echo ""
    echo "!!! ERROR !!!"
    echo "Missing \"--campaignOwner\" parameter"
    usage
    exit 1
fi

# Set Data Steward indexes
if [[ -n ${DS_IDX} ]]
then
	setIndexes "ds" ${DS_IDX}
else
    echo ""
    echo "!!! ERROR !!!"
    echo "Missing \"--dataSteward\" parameter"
    usage
    exit 1
fi

# Clean up output dir (backup mode)
BACKUP_TARGET=${BACKUP_DIR}/`date +%Y-%m-%d_%H%M%S`
if [[ -n $(ls ${OUTPUT_DIR}) ]]
then
    mkdir -p ${BACKUP_TARGET}
    mv ${OUTPUT_DIR}/* ${BACKUP_TARGET}
fi

# Generate TAC MetaSerlet JSON payload for campaign owner
CO_TEMPLATE="${TEMPLATE_DIR}/owner.json.template"
for (( INDEX = ${CO_START_IDX}; INDEX <= ${CO_STOP_IDX}; INDEX++ )); do
    generateJsonFile "owner" ${CO_TEMPLATE} ${INDEX}
done

# Generate TAC MetaSerlet JSON payload for data steward
DS_TEMPLATE="${TEMPLATE_DIR}/user.json.template"
for (( INDEX = ${DS_START_IDX}; INDEX <= ${DS_STOP_IDX}; INDEX++ )); do
    generateJsonFile "user" ${DS_TEMPLATE} ${INDEX}
done