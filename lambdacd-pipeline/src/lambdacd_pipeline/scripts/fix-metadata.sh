#!/bin/bash

FILE=$1

ffmpeg -i br0ken_licence/$FILE \
  -map_metadata 0 \
  -metadata:s:a:0 LICENSE="Licensed to the public under https://creativecommons.org/licenses/by/2.0/de/ - http://media.freifunk.net" \
  -c:a copy $FILE