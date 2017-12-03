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


(def upload-path "/srv/uploads")

(def video-path (str "/srv/videoodyssee/"(utils/uuid)))

(def video-filename "sample.mp4") ;; TODO: get video filename from uploader

;; TODO: get these fields from uploader: SUBTITLE, PERSONS, TAGS, DATE, DESCRIPTION, LINK, RELEASE_DATE
;; TODO: get generate one uuid for one pipeline run

;;
;; Steps
;;

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]
    (log/info (str "fix metadata for video: "(utils/get-video-title ctx)))
    (log/info (str "fix metadata in path "video-path))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/fixed-metadata"))
    (shell/bash ctx scripts-path
                (str "sh scripts/fix-metadata.sh "upload-path "/" video-filename " " video-path "/fixed-metadata/" video-filename))))

(defn encode-wbem [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to webm")
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_webm.sh " video-path "/fixed-metadata/" video-filename " " video-path "/processed-video/" video-filename ".webm"))))

(defn encode-h264 [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to h264")
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_h264_AAC_HQ.sh " video-path "/fixed-metadata/" video-filename " " video-path "/processed-video/" video-filename))))

(defn upload-to-cdn [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to cdn")
    (shell/bash ctx scripts-path
                (str "sh scripts/upload_video_to_cdn.sh " video-path " " cdn-url))
    ))

(defn upload-to-youtube [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to youtube")
    (shell/bash ctx cwd "exit 0")
    ))

(defn publish-to-voctoweb [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to voctoweb")
    (shell/bash ctx scripts-path (str "sh scripts/publish_videos_at_voctoweb.sh "
                                      video-path "/processed-video/" video-filename " "
                                      api-key " "
                                      api-url " "
                                      "FFF17 "  ;; TODO: get conference acronym from uploader
                                      "deu "    ;; TODO: get language from uploader
                                      "title "));; TODO: get title from uploader
    ))

(defn publish-to-socialmedia [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to socialmedia")
    (shell/bash ctx cwd "exit 0")
    ))

;; TODO: use this script to get best thumbnails: https://github.com/voc/publishing/blob/master/postprocessing/generate_thumb_autoselect_compatible.sh
(defn create-thumbnail-images [args ctx]
  (let [cwd (:cwd args)]

    (log/info "create thumbnail images")
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str"sh scripts/create_poster_thumbnails.sh " video-path "/fixed-metadata/" video-filename " " video-path "/processed-video/"))
    ))

(defn cleanup [args ctx]
  (let [cwd (:cwd args)]

    (log/info "cleanup")
    (shell/bash ctx cwd (str "rm -r " video-path))
    ))