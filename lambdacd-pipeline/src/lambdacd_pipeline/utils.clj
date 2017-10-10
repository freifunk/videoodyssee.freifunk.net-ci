(ns lambdacd-pipeline.utils
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