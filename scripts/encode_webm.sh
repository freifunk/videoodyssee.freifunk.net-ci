#!/bin/bash

SOURCE=$1
ORIGINAL_FILE_PATH=$2
TARGET=$3
TARGET_BASE=$(basename "${ORIGINAL_FILE_PATH%.*}")
PASSLOG=/tmp/${TARGET_BASE%.*}-webm-1st-pass

if [ ! -e $PASSLOG-0.log ]; then
ffmpeg  -threads 8 -analyzeduration 40000000 -probesize 100000000 -i ${SOURCE}/${TARGET_BASE}.mp4 \
  -c:v libvpx -g:0 120 -b:v  1200k -qmin:0 11 -qmax:0 51 \
  -minrate:0 100k -maxrate:0 5000k \
  -pass 1 -passlogfile  $PASSLOG \
  -c:a  libvorbis -b:a 96k  -ac:a 2  -ar:a 48000  -metadata:s:a language=de \
  -aspect  16:9  -f webm ${TARGET}/${TARGET_BASE}.webm
fi

ffmpeg -y -threads  8  -analyzeduration  40000000  -probesize  100000000  -i ${SOURCE}/${TARGET_BASE}.mp4 \
  -c:v  libvpx  -pass  2  -passlogfile  $PASSLOG \
  -g  120  -b:v  1200k  -qmin  11  -qmax  51  -minrate  100k  -maxrate  5000k \
  -c:a  libvorbis  -b:a  96k  -ac:a 2  -ar:a 48000  \
  -aspect  16:9  -f  webm ${TARGET}/${TARGET_BASE}.webm