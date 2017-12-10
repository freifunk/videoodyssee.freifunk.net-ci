#!/usr/bin/env bash

VIDEOPATH="$1"
ORIGINAL_FILE="$2"
UUID="$3"
API_KEY="$4"
API_URL="$5"
CONFERENCE_ACRONYM="$6"
LANGUAGE="$7"
TITLE="$8"
SUBTITLE="$9"
PERSONS="${10}"
TAGS="${11}"
DATE="${12}"
DESCRIPTION="${13}"
LINK="${14}"
RELEASE_DATE="${15}"
VIDEOFILE=${VIDEOPATH}/$(basename "${ORIGINAL_FILE%.*}")
LENGTH=$(printf  "%.0f" "$(ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 "${VIDEOFILE}.mp4")")
WIDTH=$(ffprobe -v error -select_streams v:0 -show_entries stream=width -of default=noprint_wrappers=1:nokey=1 "${VIDEOFILE}.mp4")
HEIGHT=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 "${VIDEOFILE}.mp4")
TITLE_SLUG="$(echo -n "${TITLE}" | sed -e 's/[^[:alnum:]]/-/g' | tr -s '-' | tr A-Z a-z)"
FILENAME=$(basename "${ORIGINAL_FILE%.*}")

cat << EOF > /tmp/${UUID}-event.json
{
    "api_key":"$API_KEY",
    "acronym":"$CONFERENCE_ACRONYM",
    "event":{
      "poster_filename":"${UUID}/${FILENAME}_preview.jpg",
      "thumb_filename":"${UUID}/${FILENAME}_thumb.jpg",
      "guid":"$UUID",
      "slug":"$TITLE_SLUG",
      "title":"$TITLE",
      "subtitle": "$SUBTITLE",
      "persons":${PERSONS},
      "tags":${TAGS},
      "date":"$DATE",
      "description":"$DESCRIPTION",
      "link":"$LINK",
      "release_date":"$RELEASE_DATE",
      "original_language": "$LANGUAGE"
    }
  }
EOF


# create event
response_code=$(curl --write-out %{http_code} --silent --output /dev/null -H "CONTENT-TYPE: application/json" -d "@/tmp/${UUID}-event.json" "${API_URL}/api/events")

if [ $response_code -ge 300 ]
then
    echo "Error sending event, status code: $response_code"
    exit 1
fi

rm /tmp/${UUID}-event.json

# add recording wbem
for FORMAT in webm mp4; do
    FILESIZE=$(( $(stat --printf="%s" "${VIDEOFILE}.${FORMAT}") / 1048576))
    cat << EOF > /tmp/${UUID}-${FORMAT}.json
        {
        "api_key":"$API_KEY",
        "guid":"$UUID",
        "recording":{
          "filename":"${FILENAME}.${FORMAT}",
          "folder":"${UUID}",
          "mime_type":"video/$FORMAT",
          "language":"$LANGUAGE",
          "size":$FILESIZE,
          "length":$LENGTH,
          "width":$WIDTH,
          "height":$HEIGHT
          }
      }
EOF
    response_code=$(curl --write-out %{http_code} --silent --output /dev/null -H "CONTENT-TYPE: application/json" -d "@/tmp/${UUID}-${FORMAT}.json" "${API_URL}/api/recordings")
    if [ $response_code -ge 300 ]
    then
        echo "Error sending event, status code: $response_code"
    exit 1
fi
    rm /tmp/${UUID}-${FORMAT}.json
done
