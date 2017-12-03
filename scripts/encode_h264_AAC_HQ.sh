#!/usr/bin/env bash

INPUT=$1
OUTPUT=$2

ffmpeg -v warning -i $INPUT -vf yadif \
  -c:v libx264 -pix_fmt yuv420p -crf:v 14 -profile:v high -level:v 4.2 \
  -c:a libfdk_aac -b:a 192k -aspect 16:9 -metadata title="<title>" \
    -metadata album="<album>" \
    -metadata copyright="Licensed to the public under http://creativecommons.org/licenses/by-sa/3.0/" \
    -f mp4 -movflags faststart $OUTPUT.mp4
