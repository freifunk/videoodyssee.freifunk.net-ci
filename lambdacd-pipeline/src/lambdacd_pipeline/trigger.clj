(ns lambdacd-pipeline.trigger
  (:require
    [clojure.tools.logging :as log]
    [clojure.core.async :as async]
    [lambdacd.stepsupport.killable :as killable]
    [lambdacd.event-bus :as event-bus]
    [ring.util.response :as ring-response]
    [compojure.core :as compojure]))

(defn notify-pipeline [ctx title]
  (log/info (str "Notify pipeline about video with title: " title))
  (event-bus/publish!! ctx :external-trigger-received {:title title :some-value "blabla"})
  ;; TODO: return current build number and maybe even more data like the request etc
  (-> (ring-response/response "bla")
      (ring-response/status 200)))

(defn external-trigger [pipeline]
  ;; TODO: use request body instead of path paramter
  (compojure/POST "/run/:title" [title]
                 (log/info (str "received external trigger with title: " title))
                 (notify-pipeline (:context pipeline) title)))


(defn- wait-for-trigger-event-while-not-killed [ctx trigger-events]
  (loop []
    (let [[result _] (async/alts!!
                      [trigger-events
                       (async/timeout 1000)]
                      :priority true)]
      (killable/if-not-killed ctx
                              (do
                                (if result
                                  (assoc (:trigger-parameters result) :status :success)
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
        ;; TODO: is it ok to close channel if we want receive data from it later on?
        _              (event-bus/unsubscribe ctx :external-trigger-received subscription)
        ]
    wait-result))
