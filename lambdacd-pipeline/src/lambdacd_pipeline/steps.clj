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

(defn current-timestamp
  "Taken from
    System/currentTimeMillis."
  []
  (quot (System/currentTimeMillis) 1000))

;;
;; Dry Run
;;

(defn dry-run?
  "can be used within steps to check if dry-run is set"
  [args]
  (= true (get-in args [:global :dry-run])))

(defn exec-step?
  "can be used within steps to check if dry-run is set"
  [args]
  (not (dry-run? args)))

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