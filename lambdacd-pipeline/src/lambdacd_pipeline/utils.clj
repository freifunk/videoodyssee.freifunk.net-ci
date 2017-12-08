(ns lambdacd-pipeline.utils
  (:require
    [lambdacd.event-bus :as event-bus]
    [clojure.tools.logging :as log]
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

(defn authenticated? [name pass]
  (and (= name "admin")
       (= pass "admin")))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn external-trigger-params [args] (get (get args :global) :external-trigger-params))

(defn get-param [args key] (get (external-trigger-params args) key))
