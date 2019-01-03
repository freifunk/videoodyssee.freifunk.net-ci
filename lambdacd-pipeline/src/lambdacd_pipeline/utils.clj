(ns lambdacd-pipeline.utils
  (:require
    [lambdacd.event-bus :as event-bus]
    [lambdacd.steps.shell :as shell]
    [clojure.tools.logging :as log]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [webjure.json-schema.validator.macro :refer [make-validator]]
    [cheshire.core :as cheshire]
    [clojure.core.async
     :as    async
     :refer [<! >! <!! timeout chan alt! go]])
  (:import (java.net InetAddress)))

;;
;; Utils
;;
(defn hostname []
  (-> (. InetAddress getLocalHost)
      (.getHostName)))

(defn ci?
  "check if the the current host is the CI server"
  ([]
   (ci? (hostname)))
  ([host]
   (.startsWith host "videoodyssee")))

(defn current-timestamp
  "Taken from
    System/currentTimeMillis."
  []
  (quot (System/currentTimeMillis) 1000))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn external-trigger-params [args] (get (get args :global) :external-trigger-params))

(defn get-param [args key] (get (external-trigger-params args) key))

(defn get-uuid [args]
      (if (nil? (get-param args "uuid"))
        (get (get args :global) "uuid")
        (get-param args "uuid")
        )
      )

(def upload-schema (io/resource
                "upload-format.schema.json" ))

(def upload-schema-validator
  (make-validator (cheshire/parse-string (slurp upload-schema)) {}))

(defn ffmpeg-get-length [cwd ctx file]
  (if (.exists (io/file file))
    (:out (shell/bash ctx cwd (str "LANG=C printf  \"%.0f\" \"$(ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 \"" file "\")\"")))
    ))

(defn ffmpeg-get-width [cwd ctx filename]
  (if (.exists (io/file filename))
      (:out (shell/bash ctx cwd(str "ffprobe -v error -select_streams v:0 -show_entries stream=width -of default=noprint_wrappers=1:nokey=1 \"" filename "\"")))
    ))

(defn ffmpeg-get-height [cwd ctx file]
  (if (.exists (io/file file))
    (:out (shell/bash ctx cwd(str "ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 \"" file "\"")))
    ))

(defn file-size-in-mb [filename]
  (if (.exists (io/file filename))
    (long (/(.length (io/file filename)) 1048576))
    ))

(defn get-basename-without-extension [filename]
  (string/replace (.getName (io/file filename)) #"(.*)\.[^.]+$" "$1"))