#!/bin/bash

# Parameters variables
ACTION_NAME=$1
RESOURCE_NAME=$2
BASTION=$3

# Path variables
WORKINGDIR="$(pwd)"
MAIN_WORKINGDIR="$(readlink -f $0 | xargs dirname)"
ANSIBLE_WORKINGDIR="${MAIN_WORKINGDIR}"
DOCKER_WORKINGDIR="${MAIN_WORKINGDIR}/docker"
TERRAFORM_WORKINGDIR="${MAIN_WORKINGDIR}/terraform"
TMP_WORKINGDIR="${MAIN_WORKINGDIR}/tmp"

# Others variables
DOCKER_USER="$(id -u):$(id -g)"
SSH_COMMON_ARGS=""
TERRAFORM_BACKEND_KEY="us-east-1/talend-cloud-test/tdq-onprem-${RESOURCE_NAME}"
USERCREATION_WORKDIR="${MAIN_WORKINGDIR}/onprem-user-creation"
WINDOWS_TEMP_PASSWORD="myTempPassword123"

test -z "${ACTION_NAME}" && echo "ERROR: ACTION_NAME is needed as first parameter" && exit 1
test -z "${RESOURCE_NAME}" && echo "ERROR: RESOURCE_NAME is needed as second parameter" && exit 1
test -z "${BASTION}" && BASTION='localhost'
test "${BASTION}" != 'localhost' && SSH_COMMON_ARGS="-o ProxyCommand='ssh -W %h:%p ${BASTION}'"

test -z "${TERRAFORM_CMD}" && TERRAFORM_CMD="docker run --rm \
  -u ${DOCKER_USER}
  -v ${HOME}/.ssh/id_rsa.pub:/.ssh/id_rsa.pub \
  -v ${TERRAFORM_WORKINGDIR}:/app/ \
  -w /app/ \
  hashicorp/terraform"

test -z "${ANSIBLE_CMD}" && ANSIBLE_CMD="docker run --rm \
  -v ${HOME}/.ssh/id_rsa:/root/.ssh/id_rsa \
  -v ${HOME}/.ssh/id_rsa.pub:/root/.ssh/id_rsa.pub \
  -v ${ANSIBLE_WORKINGDIR}:/ansible/playbooks \
  ansible_playbook" && \
  ANSIBLE_DOCKER_IMAGE_NEEDED=true

case ${ACTION_NAME} in
  destroy)
    echo "--- Destroy instance ---"
    cd ${TERRAFORM_WORKINGDIR}
    rm -rf .terraform
    echo "  > Initializing terraform..."
    ${TERRAFORM_CMD} init -backend-config="key=${TERRAFORM_BACKEND_KEY}"
    echo "  > Destroying any previous infrastructure in the namespace ${RESOURCE_NAME}..."
    ${TERRAFORM_CMD} destroy -force -target "aws_instance.testonprem_instance_${RESOURCE_NAME}"
    cd ${WORKINGDIR}
    exit 0;;
  create) ;;
  *) echo "Action '${ACTION_NAME}' is not supported" && exit 1;;
esac

if [ -z "${build_user}" ] || [ -z "${build_password}" ]; then
  echo "ERROR: a required environment variable is absent (build_user, build_password)"
  exit 1
fi

if [[ ${ANSIBLE_DOCKER_IMAGE_NEEDED} && "$(docker images -q ansible_playbook 2> /dev/null)" == "" ]]; then
  echo "--- Build docker image to run ansible ---"
  cd ${DOCKER_WORKINGDIR}
  docker build -f ansible-dockerfile -t ansible_playbook .
fi

build_domain="https://newbuild.talend.com/builds"
build_auth="$build_user:$build_password"
mkdir -p ${TMP_WORKINGDIR}

check_last_command(){
  if [ ! $? -eq 0 ]; then
     echo "ERROR: $1"
     exit 1
  fi
}

scan_folder(){
  filter=$1
  index=$(test -z "$2" && echo "1" || echo "$2")
  echo "Scanning $scan_url..."
  scan_subfolder=$(curl -s -u $build_auth $scan_url | sed -n 's/.*href="\([^"]*\).*/\1/p' | sort -r | grep -v '^/' | grep -v '^?' | egrep $filter | head -$index | tail -1)
  check_last_command "Unable to scan this folder"
  echo "  Found : $scan_subfolder"
  scan_url="$scan_url$scan_subfolder"
}

check_for_installer(){
  scan_url="$build_domain/$build_version_date/all/"
  scan_folder "^$build_version/$"
  scan_folder "^all_[0-9]+/$"
  scan_folder "^Talend\-Installer\-[0-9]{8}_[0-9]{4}\-$build_version\-installer/$"
}

echo "--- Local cleaning ---"
rm -rf ${TMP_WORKINGDIR}/*

if [ -z ${build_version} ]; then
  echo "--- Installer : Find the last version available ---"
  echo "Check for the last version"
  scan_url=$build_domain/
  scan_folder "^V[0-9\.]*SNAPSHOT_[0-9]*_[0-9]*"
  build_version=$(basename $scan_url | sed "s/^\(V[0-9\.]*SNAPSHOT\)_[0-9]*_[0-9]*$/\1/")
  test -n "$(echo $build_version)"
  check_last_command "Recent version not found !"
  echo "  Found a recent version : $build_version"
fi

if [ -z ${build_version_date} ]; then
  echo "--- Installer : Find a $build_version version build with on prem installer ---"
  for try in `seq 1 5`;
  do
    echo "Check for a recent build"
    scan_url=$build_domain/
    scan_folder "^$build_version" "$try"
    build_version_date=$(basename $scan_url | grep "^\(V[0-9\.]*SNAPSHOT\)_[0-9]*_[0-9]*$")
    test -z $build_version_date && break
    echo "  Found a recent build for $build_version : $build_version_date"
    echo "Check for a on-prem installer in that build"
    check_for_installer
    test -n "$(echo $scan_url | grep 'Talend\-Installer')" && break
    echo "  Unable to find installer for $build_version_date"
  done
else
  echo "--- Installer : Check $build_version_date build has on prem installer ---"
  check_for_installer
fi
test -n "$(echo $scan_url | grep 'Talend\-Installer')"
check_last_command "Recent installer not found !"
echo "  Found installer for $build_version_date"

installer_url=$(curl -s -u $build_auth $scan_url | sed -n 's/.*href="\([^"]*\).*/\1/p' | grep -v '^/' | grep -v '^?' | grep -v '^dist'| xargs -r -I {} echo "$scan_url{}")
dist_url="${scan_url}dist"
dist_checksum_url="${scan_url}dist.MD5"

echo "--- Installer : Download $build_version_date binaries and checksum ---"
curl --output /dev/null -s -u $build_auth --head --fail $dist_url
check_last_command "Unable to find $dist_url"
echo "$dist_url exists"
wget -nv --auth-no-challenge --user $build_user --password $build_password -P ${TMP_WORKINGDIR} $dist_checksum_url
check_last_command "Unable to download $dist_checksum_url"
wget -nv --auth-no-challenge --user $build_user --password $build_password -P ${TMP_WORKINGDIR} $installer_url
check_last_command "Unable to download $installer_url"

echo "--- Installer : Verify files ---"
ls ${TMP_WORKINGDIR}/*-installer.zip | head -1 | xargs unzip -t
check_last_command "Installer zip file is corrupted !"
dist_checksum="$(cat ${TMP_WORKINGDIR}/dist.MD5 | tr -d '\n')"
check_last_command "Dist file md5 does not exist !"

echo "--- Installer : Unpack binaries ---"
[[ ${RESOURCE_NAME} == windows* ]] && installer_extension=".exe" || installer_extension=".run"
installer_archive=$(ls ${TMP_WORKINGDIR}/*-installer.zip | head -1)
installer_file=$(unzip -l $installer_archive | awk "/$installer_extension/ {print \$4}")
unzip -d ${TMP_WORKINGDIR} $installer_archive $installer_file
check_last_command "Installer binaries unzip failed !"
rm $installer_archive

echo "--- License : Scan in $build_version_date folder ---"
scan_url="$build_domain/$build_version_date/LICENSES/"
scan_folder "^licenses_[0-9]+_30days_100users_[0-9]{8}_to_[0-9]{8}_simult.zip$"
test -n "$(echo $scan_url | grep '.zip$')"
check_last_command "Licenses not found !"

echo "--- License : Download $build_version_date file ---"
wget -nv --auth-no-challenge --user $build_user --password $build_password -P ${TMP_WORKINGDIR} $scan_url
check_last_command "License download failed !"

echo "--- License : Verify files ---"
license_archive=$(ls ${TMP_WORKINGDIR}/licenses_*.zip | head -1)
unzip -t $license_archive
check_last_command "Licenses zip file is corrupted !"

echo "--- License : Select one file ---"
license_path=$(unzip -l $license_archive | awk '/TP_ALL/ {print $4}')
license_file=$(basename $license_path)
unzip -p $license_archive $license_path > ${TMP_WORKINGDIR}/$license_file
echo "  License selected"
rm $license_archive

set -e

echo "--- Create infrastructure with terraform ---"
cd ${TERRAFORM_WORKINGDIR}
rm -rf .terraform
echo "  > Initializing terraform..."
${TERRAFORM_CMD} init -backend-config="key=${TERRAFORM_BACKEND_KEY}"
echo "  > Destroying any previous infrastructure in the namespace ${RESOURCE_NAME}..."
${TERRAFORM_CMD} destroy -force -target "aws_instance.testonprem_instance_${RESOURCE_NAME}"
echo "  > Creating infrastructure for the namespace ${RESOURCE_NAME}..."
${TERRAFORM_CMD} apply -auto-approve -target "aws_instance.testonprem_instance_${RESOURCE_NAME}"
instance_private_dns=$(${TERRAFORM_CMD} output ${RESOURCE_NAME}_private_dns)

echo "--- Wait for ec2 instance to boot ---"
cd ${ANSIBLE_WORKINGDIR}
${ANSIBLE_CMD} playbooks/${RESOURCE_NAME}/01_wait_instance.yml \
  -e "delegate_to_address=${BASTION}" \
  -e "instance_private_dns=${instance_private_dns}"

echo "--- Prepare instance ---"
cd ${ANSIBLE_WORKINGDIR}
${ANSIBLE_CMD} playbooks/${RESOURCE_NAME}/02_prepare_instance.yml -i "${instance_private_dns}," --ssh-common-args "${SSH_COMMON_ARGS}" \
  -e "ansible_password=${WINDOWS_TEMP_PASSWORD}" \
  -e "ansible_winrm_server_cert_validation=ignore" \
  -e "ansible_winrm_transport=basic" \
  -e "ansible_winrm_operation_timeout_sec=120" \
  -e "ansible_winrm_read_timeout_sec=180"

echo "--- Deploy Talend on prem ---"
cd ${ANSIBLE_WORKINGDIR}
${ANSIBLE_CMD} playbooks/${RESOURCE_NAME}/03_install_talend.yml -i "${instance_private_dns}," --ssh-common-args "${SSH_COMMON_ARGS}" \
  -e "ansible_password=${WINDOWS_TEMP_PASSWORD}" \
  -e "ansible_winrm_server_cert_validation=ignore" \
  -e "ansible_winrm_transport=basic" \
  -e "ansible_winrm_operation_timeout_sec=120" \
  -e "ansible_winrm_read_timeout_sec=180" \
  -e "build_user=${build_user}" \
  -e "build_password=${build_password}" \
  -e "installer_file=${installer_file}" \
  -e "dist_url=${dist_url}" \
  -e "dist_checksum=${dist_checksum}" \
  -e "license_file=${license_file}"

echo "--- Wait for health check to be up ---"
health_check_urls=(
  "http://${instance_private_dns}:19999/health"
  "http://${instance_private_dns}:19999/internal/data-stewardship/health"
  "http://${instance_private_dns}:19999/internal/data-history-service/health"
  "http://${instance_private_dns}:19999/internal/schemaservice/health"
  "http://${instance_private_dns}:8187/health"
)
for health_check_url in "${health_check_urls[@]}"
do
  echo "Checking $health_check_url"
  until $(test -n "$(curl --silent --max-time 5 --head $health_check_url | grep 'HTTP/[0-9.]* 200')"); do
    printf '.'
    sleep 5
  done
  echo ""
done

echo "--- User creation ---"
cp -r ${USERCREATION_WORKDIR} ${TMP_WORKINGDIR}/user-creation
cat << EOF > ${TMP_WORKINGDIR}/user-creation/conf/${instance_private_dns}.conf
TAC_URL="${instance_private_dns}"
TAC_PORT="8080"
TAC_SERVICE="org.talend.administrator"
TAC_LOGIN="security@company.com"
TAC_PASSWORD="admin"
EOF
export CONF_FILE="${instance_private_dns}.conf"
${TMP_WORKINGDIR}/user-creation/create.sh

echo "--- Local cleaning ---"
set +e
rm -rf ${TMP_WORKINGDIR}/*
cd ${WORKINGDIR}

echo "--- Save instance private dns ---"
cat << EOF > ${TMP_WORKINGDIR}/instance_private_dns
${instance_private_dns}
EOF
