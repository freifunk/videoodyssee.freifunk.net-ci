#!/usr/bin/env bash

curl -H "CONTENT-TYPE: application/json" -d '{
    "api_key":"4",
    "acronym":"frab123",
    "poster_url":"http://koeln.ccc.de/images/chaosknoten_preview.jpg",
    "thumb_url":"http://koeln.ccc.de/images/chaosknoten.jpg",
    "event":{
      "guid":"123",
      "slug":"123",
      "title":"qwerty"
    }
  }' "http://localhost:3000/api/events"


curl -H "CONTENT-TYPE: application/json" -d '{
    "api_key":"4",
    "guid":"123",
    "recording":{
      "filename":"some.mp4",
      "folder":"h264-hd",
      "mime_type":"video/mp4",
      "language":"deu"
      "size":"12",
      "length":"3600"
      }
  }' "http://localhost:3000/api/recordings"