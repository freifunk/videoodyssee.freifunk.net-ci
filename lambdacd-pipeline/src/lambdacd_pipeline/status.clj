(ns lambdacd-pipeline.status
  (:require
    [clojure.data.json :as json]))

(defn status []
  (fn [& _]
     {:status  200
      :headers {"Content-Type" "application/json"}
      :body    (json/write-str {:status "OK"})
      })
  )