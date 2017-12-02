(ns lambdacd-pipeline.steps
  (:require
   [lambdacd.steps.shell :as shell]
   [lambdacd-pipeline.utils :as utils]
   [outpace.config :refer [defconfig]]
   [clojure.tools.logging :as log])
  )

(defconfig ^:required scripts-path "/opt/pipeline")
(defconfig ^:required cdn-url "rsync://yourpersonalexample.net:/module")

(def uuid (utils/uuid))

(def upload-path "/srv/uploads")

(def video-path (str "/srv/videoodyssee/"(utils/uuid)))

(def processed-videos-path (str "/srv/videoodyssee/"(utils/uuid) "/processed-videos"))

(def video-filename "sample.mp4")

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
    (shell/bash ctx cwd "exit 0")
    ))

(defn publish-to-socialmedia [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to socialmedia")
    (shell/bash ctx cwd "exit 0")
    ))

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