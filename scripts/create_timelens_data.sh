#!/bin/bash

SOURCE=$1
ORIGINAL_FILE_PATH=$2
TARGET=$3
TARGET_BASE=$(basename "${ORIGINAL_FILE_PATH%.*}")

timelens "${SOURCE}/${TARGET_BASE}.mp4" \
  --timeline "${TARGET}/${TARGET_BASE}.timeline.jpg"\
  --thumbnails "${TARGET}/${TARGET_BASE}.vtt"
