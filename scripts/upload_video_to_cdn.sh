#!/usr/bin/env bash

VIDEO_DIR=$1
URL=$2
UUID=$3
CONFERENCE=$4

rsync -a ${VIDEO_DIR}/processed-video/ ${URL}/${CONFERENCE}/${UUID}