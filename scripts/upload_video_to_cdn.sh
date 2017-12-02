#!/usr/bin/env bash

VIDEO_DIR=$1
URL=$2
UUID=$(basename "$VIDEO_DIR")

rsync -a ${VIDEO_DIR}/processed-video/ ${URL}/${UUID}