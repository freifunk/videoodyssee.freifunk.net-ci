(ns lambdacd-pipeline.api
  (:require
    [lambdacd.event-bus :as event-bus]
    [ring.util.response :as ring-response]
    [clojure.data.json :as json]
    [lambdacd.presentation.pipeline-state :as pipeline-state]
    [lambdacd.state.core :as state]
    [compojure.core  :refer [routes GET context]]
    [clojure.tools.logging :as log]))

;;;;;;;;; core states
(defn all-build-numbers [pipeline]
  (let [all-builds (state/all-build-numbers (:context pipeline))]
    (json/write-str {:all-build-numbers all-builds})))

(defn next-build-number [pipeline]
  (let [next-build (state/next-build-number (:context pipeline))]
    (json/write-str {:next-build-number next-build})))

(defn build-metadata [pipeline build-number]
  (let [ctx (:context pipeline)
        meta-data (state/get-build-metadata  ctx build-number)]
    (json/write-str {:meta-data meta-data
                     :build-number build-number})))

(defn latest-build-metadata [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        meta-data (state/get-build-metadata  ctx current-build)]
    (json/write-str {:meta-data meta-data
                     :build-number current-build})))

(defn pipeline-structure [pipeline build-number]
  (let [ctx (:context pipeline)
        pipeline-structure (state/get-build-metadata  ctx build-number)]
    (json/write-str {:pipeline-structure pipeline-structure
                     :build-number build-number})))

(defn latest-pipeline-structure [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        pipeline-structure (state/get-build-metadata  ctx current-build)]
    (json/write-str {:pipeline-structure pipeline-structure
                     :build-number current-build})))

(defn step-results [pipeline build-number]
  (let [ctx (:context pipeline)
        step-results (state/get-step-results  ctx build-number)]
    (json/write-str {:step-results step-results
                     :build-number build-number})))

(defn latest-step-results [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        step-results (state/get-step-results  ctx current-build)]
    (json/write-str {:step-results step-results
                     :build-number current-build})))

;;;;;;;;; pipeline states
(defn latest-build-duration [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        step-results (state/get-step-results ctx current-build)
        duration (pipeline-state/build-duration step-results)]
    (json/write-str {:duration duration
                     :build-number current-build})))

(defn build-duration [pipeline build-number]
  (let [ctx (:context pipeline)
        step-results (state/get-step-results ctx build-number)
        duration (pipeline-state/build-duration step-results)]
    (json/write-str {:duration duration
                     :build-number build-number})))

(defn start-time [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        step-results (state/get-step-results ctx current-build)
        earliest-first-update (pipeline-state/earliest-first-update step-results)]
    (json/write-str {:earliest-first-update earliest-first-update
                     :build-number current-build})))

(defn end-time [pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        step-results (state/get-step-results ctx current-build)
        latest-most-recent-update (pipeline-state/latest-most-recent-update step-results)]
    (json/write-str {:latest-most-recent-update latest-most-recent-update
                     :build-number current-build})))

(defn history [pipeline]
  (let [ctx (:context pipeline)
        history (pipeline-state/history-for ctx)]
    (json/write-str history)))

(defn overall-build-status[pipeline build-number]
  (let [ctx (:context pipeline)
        step-results (state/get-step-results ctx build-number)
        overall-build-status (pipeline-state/overall-build-status step-results)]
    (json/write-str {:overall-build-status overall-build-status
                     :build-number build-number})))

(defn latest-overall-build-status[pipeline]
  (let [ctx (:context pipeline)
        current-build (unchecked-subtract (state/next-build-number ctx) 1)
        step-results (state/get-step-results ctx current-build)
        overall-build-status (pipeline-state/overall-build-status step-results)]
    (json/write-str {:overall-build-status overall-build-status
                     :build-number current-build})))

;;;;;;;;; request mapping
(defn api [pipeline]
  (GET "/rest-api/:endpoint" [endpoint]
       (log/info (str "api function '" endpoint "' called"))
       (fn [& _]
           {:status  200
            :headers {"Content-Type" "application/json"}
            :body    ((resolve (symbol "lambdacd-pipeline.api"endpoint)) pipeline)
            })))

