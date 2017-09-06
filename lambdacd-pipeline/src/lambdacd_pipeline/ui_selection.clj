(ns lambdacd-pipeline.ui-selection
    (:require
        [hiccup.core :as h]
        [lambdaui.core :as lambdaui]
        [lambdacd.ui.core :as reference-ui]
        [ring.util.response :as response]
        [compojure.core :refer [routes GET context]])
    (:gen-class))

(defn ui-routes [pipeline cctray]
    (let [lambdaui-app    (lambdaui/ui-for pipeline :contextPath "/lambdaui")
          referenceui-app (reference-ui/ui-for pipeline)]
        (routes
         (GET "/" [] (response/redirect "/lambdaui/lambdaui/index.html"))
         (core/notifications-for pipeline)
         (GET "/cctray/pipeline.xml" [] cctray)
         (context "/lambdaui" [] lambdaui-app)
         (context "/reference" [] referenceui-app))))
