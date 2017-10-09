(ns lambdacd-pipeline.steps
  (:require
   [lambdacd.steps.shell :as shell]
   [lambdacd-git.core :as lambdacd-git]
   [lambdacd-pipeline.git :as git]
   [clojure.tools.logging :as log])
  (:import (java.net InetAddress)))

;;
;; Utils
;;
(defn hostname []
  (-> (. InetAddress getLocalHost)
      (.getHostName)))
(def script-path "src/lambdacd_pipeline/scripts/")

(def upload-path "unprocessed-videos")

(def fixed-metadata-path "/srv/fixed-metadata")

(def processed-videos-path "/srv/processed-videos")

(def video-filename "sample.mp4")


(defn current-timestamp
  "Taken from
    System/currentTimeMillis."
  []
  (quot (System/currentTimeMillis) 1000))

;;
;; GIT
;;
(def repo-uri "https://github.com/freifunk/videoodyssee.freifunk.net-ci.git")
(def repo-branch "master")

;; https://github.com/flosell/lambdacd-git
(defn wait-for-repo [args ctx]
  (lambdacd-git/wait-for-git ctx repo-uri :ms-between-poll (* 60 1000) :ref (str "refs/heads/" repo-branch)))

(defn clone [args ctx]
  (lambdacd-git/clone ctx repo-uri (:revision args) (:cwd args)))

;;
;; Steps
;;

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]

    (log/info "fix metadata")
    (shell/bash ctx cwd
                (str "sh scripts/fix-metadata.sh "upload-path "/" video-filename " " fixed-metadata-path "/" video-filename))))

(defn encode-wbem [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to webm")
    (shell/bash ctx cwd
                (str "sh scripts/encode_webm.sh " fixed-metadata-path "/" video-filename " " processed-videos-path "/" video-filename ".webm"))))

(defn encode-h264 [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to h264")
    (shell/bash ctx cwd
                (str "sh scripts/encode_h264_AAC_HQ.sh " fixed-metadata-path "/" video-filename " " processed-videos-path "/" video-filename))))

(defn upload-to-cdn [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to cdn")
    ))

(defn upload-to-youtube [args ctx]
  (let [cwd (:cwd args)]

    (log/info "upload to youtube")
    ))

(defn publish-to-voctoweb [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to voctoweb")
    ))

(defn publish-to-socialmedia [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to socialmedia")
    ))

(defn cleanup [args ctx]
  (let [cwd (:cwd args)]

    (log/info "cleanup")
    (shell/bash ctx cwd (str "rm " fixed-metadata-path "/" video-filename))
    (shell/bash ctx cwd (str "rm " processed-videos-path "/" video-filename))
    (shell/bash ctx cwd (str "rm " processed-videos-path "/" video-filename ".webm"))
    ))