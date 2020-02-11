#!/usr/bin/env bash

function die() {
    echo $1
    exit 1
}

TMP_DIR=$(mktemp -d)

echo "getting latest zip..."
curl -L  -# "https://dailymotion.github.io/kinta/zip/latest.zip" > ${TMP_DIR}/kinta.zip

unzip -q -d ${TMP_DIR} ${TMP_DIR}/kinta.zip || die "corrupted archive"

KINTADIR="$HOME/.kinta"
mkdir $KINTADIR 2> /dev/null
# This mv is a small hack to get rid of the version from the distZip
# It will remove one level of directory
mv  ${TMP_DIR}/kinta-* ${KINTADIR}/current || die "you already have an install in $KINTADIR, run 'kinta update' instead"

${KINTADIR}/current/bin/kinta bootstrap

rm -rf "$TMP_DIR"
