(ns lambdacd-pipeline.steps
  (:require
   [lambdacd.steps.shell :as shell]
   [lambdacd-pipeline.utils :as utils]
   [outpace.config :refer [defconfig]]

   [clojure.tools.logging :as log])
  )

(defconfig ^:required scripts-path "/opt/pipeline")
(defconfig ^:required cdn-url "rsync://yourpersonalexample.net:/module")
(defconfig ^:required api-url "https://your-api-url")
(defconfig ^:required api-key "your-api-key")


(def video-base-path "/srv/videoodyssee/")

;;
;; Steps
;;

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]
    (log/info (str "uuid: " (utils/get-uuid args)))
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/fixed-metadata"))
    (shell/bash ctx scripts-path
                (str "sh scripts/fix-metadata.sh " (utils/get-param args "videoFilePath") " " video-path "/fixed-metadata"))))

(defn encode-wbem [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to webm")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_webm.sh " video-path "/fixed-metadata "
                     (utils/get-param args "videoFilePath") " "
                     video-path "/processed-video"))))

(defn encode-h264 [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to h264")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_h264_AAC_HQ.sh " video-path "/fixed-metadata "
                     (utils/get-param args "videoFilePath") " "
                     video-path "/processed-video "
                     (utils/get-param args "title")))))

(defn upload-to-cdn [args ctx]
  (let [cwd (:cwd args)]

    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx scripts-path
                (str "sh scripts/upload_video_to_cdn.sh " video-path " "
                     cdn-url " "
                     (utils/get-uuid args)))
    ))

(defn upload-to-youtube [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to youtube")
    (shell/bash ctx cwd "exit 0")
    ))

(defn publish-to-voctoweb [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to voctoweb")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx scripts-path (str "sh scripts/publish_videos_at_voctoweb.sh "
                                      video-path "/processed-video "
                                      "\""(utils/get-param args "videoFilePath") "\" "
                                      (utils/get-uuid args) " "
                                      api-key " "
                                      api-url " "
                                      (utils/get-param args "conferenceAcronym") " "
                                      (utils/get-param args "language") " "
                                      "\""(utils/get-param args "title") "\" "
                                      "\""(utils/get-param args "subtitle") "\" "
                                      (utils/vector-to-json-array (utils/get-param args "persons")) " "
                                      (utils/vector-to-json-array (utils/get-param args "tags")) " "
                                      "\""(utils/get-param args "date") "\" "
                                      "\""(utils/get-param args "description") "\" "
                                      (utils/get-param args "link") " "
                                      "\""(utils/get-param args "releaseDate") "\" "))
    ))

(defn publish-to-socialmedia [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to socialmedia")
    (shell/bash ctx cwd "exit 0")
    ))

(defn create-thumbnail-images [args ctx]
  (let [cwd (:cwd args)]

    (log/info "create thumbnail images")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str"sh scripts/create_poster_thumbnails.sh " video-path "/fixed-metadata "
                    (utils/get-param args "videoFilePath") " "
                    video-path "/processed-video"))
    ))

(defn cleanup [args ctx]
  (let [cwd (:cwd args)]

    (log/info "cleanup")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "rm -r " video-path))
    ))