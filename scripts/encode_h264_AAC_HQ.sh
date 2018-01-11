#!/usr/bin/env bash

SOURCE=$1
ORIGINAL_FILE_PATH=$2
TARGET=$3
TITLE=$4
TARGET_BASE=$(basename "${ORIGINAL_FILE_PATH%.*}")

ffmpeg -v warning -i "${SOURCE}/${TARGET_BASE}.mp4" -vf yadif \
  -c:v libx264 -pix_fmt yuv420p -crf:v 23 -profile:v high -level:v 4.2 \
  -c:a libfdk_aac -b:a 128k -metadata title="'${TITLE}'" \
    -metadata album="<album>" \
    -metadata copyright="Licensed to the public under http://creativecommons.org/licenses/by-sa/3.0/" \
    -f mp4 -movflags faststart "${TARGET}/${TARGET_BASE}.mp4"
