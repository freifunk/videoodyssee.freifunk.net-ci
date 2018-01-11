#!/bin/bash

SOURCE=$1
ORIGINAL_FILE_PATH=$2
TARGET=$3
TARGET_BASE=$(basename "${ORIGINAL_FILE_PATH%.*}")
PASSLOG="/tmp/${TARGET_BASE%.*}-webm-1st-pass"

if [ ! -e "$PASSLOG-0.log" ]; then
ffmpeg -analyzeduration 40000000 -probesize 100000000 -i "${SOURCE}/${TARGET_BASE}.mp4" \
  -c:v libvpx -threads 16 -g:0 120 -b:v  1200k -qmin:0 11 -qmax:0 51 \
  -minrate:0 100k -maxrate:0 5000k \
  -pass 1 -passlogfile  "$PASSLOG" \
  -c:a  libvorbis -b:a 96k  -ac:a 2  -ar:a 48000  -metadata:s:a language=de \
    -f webm /dev/null
fi

ffmpeg -y -analyzeduration  40000000  -probesize  100000000  -i "${SOURCE}/${TARGET_BASE}.mp4" \
  -c:v  libvpx -threads 16  -pass  2  -passlogfile  "$PASSLOG" \
  -g  120  -b:v  1200k  -qmin  11  -qmax  51  -minrate  100k  -maxrate  5000k \
  -c:a  libvorbis  -b:a  96k  -ac:a 2  -ar:a 48000  \
  -f  webm "${TARGET}/${TARGET_BASE}.webm"