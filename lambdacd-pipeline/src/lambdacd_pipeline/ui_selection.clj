(ns lambdacd-pipeline.ui-selection
    (:require
        [hiccup.core :as h]
        [lambdaui.core :as lambdaui]
        [lambdacd.ui.core :as reference-ui]
        [ring.util.response :as response]
        [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
        [lambdacd-pipeline.pipeline :as pipeline]
        [lambdacd-pipeline.auth :as auth]
        [lambdacd-pipeline.status :as status]
        [lambdacd-cctray.core :as cctray]
        [lambdacd-pipeline.trigger :as trigger]
        [lambdacd-pipeline.api :as api]
        [compojure.core :refer [routes GET context]])
    (:gen-class))

(defn ui-routes-protected [pipeline]
    (let [lambdaui-app    (lambdaui/ui-for pipeline :contextPath "/lambdaui")
          referenceui-app (reference-ui/ui-for pipeline)]
        (routes
         (trigger/external-trigger pipeline)
         (GET "/" [] (response/redirect "/lambdaui/lambdaui/index.html"))
         (GET "/cctray/pipeline.xml" [] (cctray/cctray-handler-for pipeline))
         (context "/lambdaui" [] lambdaui-app)
         (context "/reference" [] referenceui-app)
         )))

(defn ui-routes   [pipeline]
  (let [lambdaui-app    (lambdaui/ui-for pipeline :contextPath "/lambdaui")
        referenceui-app (reference-ui/ui-for pipeline)]
    (routes
     (api/api pipeline)
     (api/api-buildnumbers pipeline)
     (GET "/status" [] (status/status))
     (wrap-basic-authentication (ui-routes-protected pipeline) auth/is-valid-user?)
     )))
