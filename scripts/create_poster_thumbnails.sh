#!/usr/bin/env bash

INPUT=$1
OUTPUTPATH=$2
OUTPUTFILE=$(basename $1)

ffmpeg -i $INPUT -vf  "thumbnail,scale=1920:1080" -frames:v 1 -f image2pipe -vcodec png - | convert - $(OUTPUTPATH)/$(OUTPUTFILE)_preview.jpg

ffmpeg -i $INPUT -vf  "thumbnail,scale=400:225" -frames:v 1 -f image2pipe -vcodec png - | convert - $(OUTPUTPATH)/$(OUTPUTFILE)_thumb.jpg
