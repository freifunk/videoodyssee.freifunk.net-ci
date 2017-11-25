(ns lambdacd-pipeline.ui-selection
    (:require
        [hiccup.core :as h]
        [lambdaui.core :as lambdaui]
        [lambdacd.ui.core :as reference-ui]
        [ring.util.response :as response]
        [lambdacd-pipeline.pipeline :as pipeline]
        [lambdacd-cctray.core :as cctray]
        [lambdacd-pipeline.trigger :as trigger]
        [lambdacd-pipeline.api :as api]
        [compojure.core :refer [routes GET context]])
    (:gen-class))

(defn ui-routes [pipeline]
    (let [lambdaui-app    (lambdaui/ui-for pipeline :contextPath "/lambdaui")
          referenceui-app (reference-ui/ui-for pipeline)]
        (routes
         (trigger/external-trigger pipeline)
         (api/api pipeline)
         (GET "/" [] (response/redirect "/lambdaui/lambdaui/index.html"))
         (GET "/cctray/pipeline.xml" [] (cctray/cctray-handler-for pipeline))
         (context "/lambdaui" [] lambdaui-app)
         (context "/reference" [] referenceui-app)
         )))
