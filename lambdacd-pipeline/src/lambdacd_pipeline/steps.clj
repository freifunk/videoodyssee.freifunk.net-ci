(ns lambdacd-pipeline.steps
  (:require
   [lambdacd.steps.shell :as shell]
   [lambdacd-pipeline.utils :as utils]
   [outpace.config :refer [defconfig]]
   [clojure.tools.logging :as log])
  )

(defconfig ^:required scripts-path "/opt/pipeline")

(def upload-path "/srv/uploads")

(def fixed-metadata-path (str "/srv/videoodyssee/fixed-metadata/" (utils/uuid)))

(def processed-videos-path (str "/srv/videoodyssee/processed-videos/" (utils/uuid)))

(def video-filename "sample.mp4")

;;
;; Steps
;;

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]
    (log/info (str "fix metadata for video: "(utils/get-video-title ctx)))
    (log/info "fix metadata")
    (shell/bash ctx cwd (str "mkdir -p " fixed-metadata-path))
    (shell/bash ctx scripts-path
                (str "sh scripts/fix-metadata.sh "upload-path "/" video-filename " " fixed-metadata-path "/" video-filename))))

(defn encode-wbem [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to webm")
    (shell/bash ctx cwd (str "mkdir -p " processed-videos-path))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_webm.sh " fixed-metadata-path "/" video-filename " " processed-videos-path "/" video-filename ".webm"))))

(defn encode-h264 [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to h264")
    (shell/bash ctx cwd (str "mkdir -p " processed-videos-path))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_h264_AAC_HQ.sh " fixed-metadata-path "/" video-filename " " processed-videos-path "/" video-filename))))

(defn upload-to-cdn [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to cdn")
    (shell/bash ctx cwd "exit 0")
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
    (shell/bash ctx cwd (str "mkdir -p " processed-videos-path))
    (shell/bash ctx scripts-path "sh scripts/create_poster_thumbnails.sh " fixed-metadata-path "/" video-filename " " processed-videos-path "/")
    ))

(defn cleanup [args ctx]
  (let [cwd (:cwd args)]

    (log/info "cleanup")
    (shell/bash ctx cwd (str "rm -r" fixed-metadata-path))
    (shell/bash ctx cwd (str "rm -r" processed-videos-path))
    ))