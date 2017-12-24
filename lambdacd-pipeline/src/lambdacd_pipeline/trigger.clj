(ns lambdacd-pipeline.trigger
  (:require
    [clojure.tools.logging :as log]
    [clojure.core.async :as async]
    [lambdacd.stepsupport.killable :as killable]
    [lambdacd.presentation.pipeline-state :as pipeline-state]
    [lambdacd.event-bus :as event-bus]
    [ring.util.response :as ring-response]
    [ring.util.request :as ring-request]
    [clojure.data.json :as json]
    [lambdacd.state.core :as state]
    [cheshire.core :as cheshire]
    [lambdacd-pipeline.utils :as utils]
    [compojure.core :as compojure]))

(defn notify-pipeline [ctx json-body]
  (event-bus/publish!! ctx :external-trigger-received {:external-trigger-params json-body})
  (let [current-build-number    (unchecked-subtract (state/next-build-number ctx) 1)
        build-number-as-json (json/write-str {:build-number current-build-number} :escape-unicode true?)]
  (-> (ring-response/response build-number-as-json)
        (ring-response/status 200))))

(defn external-trigger [pipeline]
  (compojure/POST "/run" request
                  (let [request-body (slurp (:body request))
                        json-body (json/read-str request-body)]
                    (if (empty? (utils/upload-schema-validator (cheshire/parse-string request-body)))
                      (notify-pipeline (:context pipeline) json-body)
                      (-> (ring-response/response "Invalid request body!") (ring-response/status 400 ))))))


(defn- wait-for-trigger-event-while-not-killed [ctx trigger-events]
  (loop []
    (let [[result _] (async/alts!!
                      [trigger-events
                       (async/timeout 1000)]
                      :priority true)]
      (killable/if-not-killed ctx
                              (do
                                (if result
                                  result
                                  (recur)))))))

(defn ^{:display-type :manual-trigger} wait-for-external-trigger
  "Build step that waits for someone to trigger a build manually, usually by clicking a button in a UI that supports it."
  [_ ctx & _]
  (let [result-ch      (:result-channel ctx)
        subscription   (event-bus/subscribe ctx :external-trigger-received)
        trigger-events (event-bus/only-payload subscription)
        _              (async/>!! result-ch [:status :waiting])
        _              (async/>!! result-ch [:out (str "Waiting for trigger...")])
        wait-result    (wait-for-trigger-event-while-not-killed ctx trigger-events)
        _              (event-bus/unsubscribe ctx :external-trigger-received subscription)]
    (merge {:status :success} {:global (merge wait-result {"uuid" (utils/uuid)})})))
