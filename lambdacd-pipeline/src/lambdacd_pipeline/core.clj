(ns lambdacd-pipeline.core
  (:require
    [lambdacd-pipeline.pipeline :as pipeline]
    [lambdacd-pipeline.utils :as utils]
    [lambdacd-pipeline.auth :as auth]
    [lambdacd-pipeline.ui-selection :as ui-selection]
    [org.httpkit.server :as http-kit]
    [lambdacd-mongodb.mongodb-state :as mongodb-state]
    [lambdacd.runners :as runners]
    [lambdacd.util :as util]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
    [ring.server.standalone :as ring-server]
    [lambdacd.core :as lambdacd]
    [lambdacd.ui.ui-server :as ui]
    [clojure.tools.logging :as log]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [outpace.config :refer [defconfig]])
  (:gen-class))

(defconfig ^:required mongodb-user "user")
(defconfig ^:required mongodb-password "secret")
(defconfig ^:required mongodb-host "mongodb://localhost:27017/lambdacd")

(defn -main [& args]
  (let [pipeline    pipeline/pipeline-def

        ;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        home-dir    (util/create-temp-dir)

        mongodb-cfg {:user         mongodb-user
                     :uri          mongodb-host
                     :password     mongodb-password
                     :hosts        ["localhost"]
                     :port         27017
                     :db           "lambdacd"
                     :col          "videoodyssee"
                     :max-builds   10
                     :ttl          10
                     :mark-running-steps-as :killed
                     :pipeline-def pipeline
                     :persist-the-output-of-running-steps false
                     :use-readable-build-numbers true}

        config      {:mongodb-cfg mongodb-cfg
                     :home-dir    home-dir
                     :name        "Freifunk - Video Odyssee"

                     :ui-config   {:expand-active-default   true

                                   :expand-failures-default true

                                   :navbar                  {:links [{:url  "https://github.com/freifunk/videoodyssee.freifunk.net-ci"
                                                                      :text "Github Repo"}]}}}

        ;; initialize and wire everything together
        pipeline    (lambdacd.core/assemble-pipeline pipeline config (mongodb-state/new-mongodb-state config))

        ;; create a Ring handler for the UI
        app          (ui-selection/ui-routes pipeline)]

    (log/info "LambdaCD Home Directory is" home-dir)

    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-new-run-after-first-step-finished pipeline)

    ;; start the webserver to serve the UI
    ;; use 'app' as handler to deactivate login
    (http-kit/run-server app
                         {:open-browser? false
                          :port          8090})))
