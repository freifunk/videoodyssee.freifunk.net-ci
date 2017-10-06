(ns lambdacd-pipeline.core
  (:require
    [lambdacd-pipeline.pipeline :as pipeline]
    [lambdacd-pipeline.ui-selection :as ui-selection]
    [lambdacd-pipeline.git :as git]
    [org.httpkit.server :as http-kit]
    [lambdacd.runners :as runners]
    [lambdacd.util :as util]
    [lambdacd.core :as lambdacd]
    [lambdacd-cctray.core :as cctray]
    [clojure.tools.logging :as log]
    [clojure.java.io :as io]
    [clojure.string :as str])
  (:gen-class))

(defn -main [& args]
  (let [pipeline                pipeline/pipeline-def

        ;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        home-dir                (util/create-temp-dir)

        config                  {:home-dir    home-dir
                                 :name        "Freifunk - Video Odyssee"

                                 :ui-config   {:expand-active-default   true

                                               :expand-failures-default true

                                               :navbar                  {:links [{:url  "https://github.com/freifunk/videoodyssee.freifunk.net-ci"
                                                                                  :text "Github Repo"}]}}}

        ;; initialize and wire everything together
        pipeline                (lambdacd.core/assemble-pipeline pipeline config)

        cctray-pipeline-handler (cctray/cctray-handler-for pipeline)

        ;; create a Ring handler for the UI
        app                     (ui-selection/ui-routes pipeline cctray-pipeline-handler)]
    (log/info "LambdaCD Home Directory is" home-dir)

    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another pipeline)

    ;; start the webserver to serve the UI
    (http-kit/run-server app
                         {:open-browser? false
                          :port          8090})))
