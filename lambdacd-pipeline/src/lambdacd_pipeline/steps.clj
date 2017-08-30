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

(defn current-timestamp
  "Taken from
    System/currentTimeMillis."
  []
  (quot (System/currentTimeMillis) 1000))

;;
;; Steps
;;
(defn hello-world [{cwd :cwd} ctx]
  (shell/bash ctx cwd "echo hello world")
  )

(defn fix-metadata [{cwd :cwd} ctx]
  (shell/bash ctx cwd (str "ls" cwd " " script-path "fix-metadata.sh /opt/videouploads/TestImage2141.mp4"))
  ;;(shell/bash ctx cwd (str script-path "/fix-metadata.sh /opt/videouploads/TestImage2141.mp4"))
  )

;;
;; GIT
;;
(def repo-uri "https://github.com/freifunk/videoodyssee.freifunk.net-ci.git")
(def repo-branch "master")

;; https://github.com/flosell/lambdacd-git
(defn wait-for-repo [args ctx]
  (lambdacd-git/wait-for-git ctx repo-uri :ref (str "refs/heads/" repo-branch)))

(defn clone [args ctx]
  (let [revision      (:revision args)

        cwd           (:cwd args)

        ref           (or revision repo-branch)

        step-result   (lambdacd-git/clone ctx repo-uri ref cwd)]

    ))