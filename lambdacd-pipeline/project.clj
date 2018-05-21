(defproject lambdacd-pipeline "1.0.0-SNAPSHOT"
  :description "A pipeline to process videos and publish them to our media portal"

  :url "http://example.com/FIXME"

  :dependencies [[lambdacd "0.14.0"]
                 [lambdaui "1.1.0"]
                 [http-kit "2.3.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.slf4j/slf4j-api "1.7.25"]
                 [ch.qos.logback/logback-core "1.2.3"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [lambdacd-mongodb "2.0.0"]
                 [lambdacd-cctray "0.5.0"]
                 [ring-basic-authentication "1.0.5"]
                 [proto-repl "0.3.1"]
                 [com.outpace/config "0.11.0"]
                 [webjure/json-schema "0.7.4"]
                 [crypto-password "0.2.0"]
                 [com.draines/postal "2.0.2"]
                 [org.clojure/data.json "0.2.6"]]

  :exclusions [org.slf4j/slf4j-simple]

  :profiles {:uberjar {:aot :all}}

  :aliases {"config" ["run" "-m" "outpace.config.generate"]}

  :main lambdacd-pipeline.core)
