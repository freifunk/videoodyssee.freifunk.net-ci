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
;; GIT
;;
(def repo-uri "https://github.com/christian-draeger/videoodyssee-test-repo.git")
(def repo-branch "master")

;; https://github.com/flosell/lambdacd-git
(defn wait-for-repo [args ctx]
  (lambdacd-git/wait-for-git ctx repo-uri :ref (str "refs/heads/" repo-branch)))

(defn clone [args ctx]
  (lambdacd-git/clone ctx repo-uri (:revision args) (:cwd args)))

;;
;; Steps
;;
(defn hello-world [{cwd :cwd} ctx]
  (shell/bash ctx cwd "echo hello world")
  )

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]

    (log/info "fix metadata")
    (shell/bash ctx cwd
                "./scripts/fix-metadata.sh sample.mp4")))
