(ns lambdacd-pipeline.utils
  (:require
    [lambdacd.event-bus :as event-bus]
    [clojure.tools.logging :as log]
    [clojure.java.io :as io]
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

(defn get-uuid [args] (get (get args :global) "uuid"))

(def upload-schema (io/resource
                "upload-format.schema.json" ))

(def upload-schema-validator
  (make-validator (cheshire/parse-string (slurp upload-schema)) {}))

(defn vector-to-json-array [vector]
  (str "['"(clojure.string/join  "','"vector) "']"))