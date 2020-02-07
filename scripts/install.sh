#!/usr/bin/env bash

TMP_DIR=$(mktemp -d)

curl -L -s "https://dailymotion.github.io/kinta/kinta.zip" > ${TMP_DIR}/kinta.zip

unzip -d ${TMP_DIR} ${TMP_DIR}/kinta.zip

KINTADIR="$HOME/.kinta"
mkdir $KINTADIR
# This mv is a small hack to get rid of the version from the distZip
# It will remove one level of directory
mv  ${TMP_DIR}/kinta-* ${KINTADIR}/current

${KINTADIR}/current/bin/kinta bootstrap

rm -rf "$TMP_DIR"
