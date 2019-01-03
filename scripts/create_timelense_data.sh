#!/bin/bash

INPUTFILE=$1
OUTPUTPATH=$2
OUTPUTFILE=$(basename "${INPUTFILE%.*}")

timelense "${INPUTFILE}" \
  --timeline "${OUTPUTPATH}/${OUTPUTFILE}.timeline.jpg"\
  --thumbnails "${OUTPUTPATH}/${OUTPUTFILE}.vtt"
