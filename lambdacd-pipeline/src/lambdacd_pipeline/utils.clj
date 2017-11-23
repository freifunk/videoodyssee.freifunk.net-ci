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

(defn get-video-title [ctx]
  (let [subscription (event-bus/subscribe ctx :external-trigger-received)
        payloads     (event-bus/only-payload subscription)]
    ;; TODO: how to get data from event bus or from async channel
    (log/info "payload is " (get (async/go [(async/<! payloads)]) :title))))

(defn current-timestamp
  "Taken from
    System/currentTimeMillis."
  []
  (quot (System/currentTimeMillis) 1000))

(defn authenticated? [name pass]
  (and (= name "admin")
       (= pass "admin")))
