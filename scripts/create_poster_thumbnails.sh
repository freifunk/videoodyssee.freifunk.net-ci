#!/usr/bin/env bash

INPUT=$1
OUTPUTPATH=$2
OUTPUTFILE=$(basename "$INPUT")

ffmpeg -i $INPUT -vf  "thumbnail,scale=1920:1080" -frames:v 1 -f image2pipe -vcodec png - | convert - "$OUTPUTPATH/$OUTPUTFILE_preview.jpg"

ffmpeg -i $INPUT -vf  "thumbnail,scale=400:225" -frames:v 1 -f image2pipe -vcodec png - | convert - "$OUTPUTPATH/$OUTPUTFILE_thumb.jpg"
