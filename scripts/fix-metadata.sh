#!/bin/bash

INPUTFILE=$1
OUTPUTFILE=$2

ffmpeg -i ${INPUTFILE} \
  -map_metadata 0 \
  -metadata:s:a:0 LICENSE="Licensed to the public under https://creativecommons.org/licenses/by/2.0/de/ - http://media.freifunk.net" \
  -c:a copy ${OUTPUTFILE}