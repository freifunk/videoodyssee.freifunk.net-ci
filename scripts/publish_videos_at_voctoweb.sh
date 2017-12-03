#!/usr/bin/env bash

VIDEOFILE=$1
API_KEY=$2
API_URL=$3
STATIC_URL=$4
CONFERENCE_ACRONYM=$5
LANGUAGE=$6
TITLE=$7
SUBTITLE=$8
PERSONS=$9
TAGS=${10}
DATE=${11}
DESCRIPTION=${12}
LINK=${13}
RELEASE_DATE=${14}
LENGTH=$(printf  "%.0f" "$(ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 "${FILENAME}.mp4")")
WIDTH=$(ffprobe -v error -select_streams v:0 -show_entries stream=width -of default=noprint_wrappers=1:nokey=1 "${VIDEOFILE}.mp4")
HEIGHT=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 "${VIDEOFILE}.mp4")
TITLE_SLUG="$(echo -n "${TITLE}" | sed -e 's/[^[:alnum:]]/-/g' | tr -s '-' | tr A-Z a-z)"
UUID=$(basename "$(dirname "$(dirname "${VIDEOFILE}.mp4")")")
FILENAME=$(basename "$VIDEOFILE")

# create event
curl -H "CONTENT-TYPE: application/json" -d '{
    "api_key":"'$API_KEY'",
    "acronym":"'$CONFERENCE_ACRONYM'",
    "event":{
      "poster_url":"'$STATIC_URL'/'$UUID'/'$FILENAME'_preview.jpg",
      "thumb_url":"'$STATIC_URL'/'$UUID'/'$FILENAME'_thumb.jpg",
      "guid":"'$UUID'",
      "slug":"'$TITLE_SLUG'",
      "title":"'$TITLE'",
      "subtitle": "'$SUBTITLE'",
      "persons":"'$PERSONS'",
      "tags":"'$TAGS'",
      "date":"'$DATE'",
      "description":"'$DESCRIPTION'",
      "link":"'$LINK'",
      "release_date":"'$RELEASE_DATE'",
      "original_language": "'$LANGUAGE'"
    }
  }' "${API_URL}/api/events"

# add recording wbem
for FORMAT in webm mp4; do
    FILESIZE=$(stat --printf="%s" "${FILENAME}.${FORMAT}")
    curl -H "CONTENT-TYPE: application/json" -d '{
        "api_key":"'$API_KEY'",
        "guid":"'$UUID'",
        "recording":{
          "filename":"'"${FILENAME}.${FORMAT}"'",
          "folder":"'$UUID'",
          "mime_type":"video/'$FORMAT'",
          "language":"'$LANGUAGE'",
          "size":'$FILESIZE',
          "length":'$LENGTH',
          "width":'$WIDTH',
          "height":'$HEIGHT'
          }
      }' "${API_URL}/api/recordings";
done
