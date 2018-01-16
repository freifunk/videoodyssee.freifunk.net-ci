(defproject lambdacd-pipeline "1.0.0-SNAPSHOT"
  :description "A pipeline to process videos and publish them to our media portal"

  :url "http://example.com/FIXME"

  :dependencies [[lambdacd "0.13.5"]
                 [lambdaui "1.0.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-core "1.0.13"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [lambdacd-mongodb "2.0.0"]
                 [lambdacd-cctray "0.4.2"]
                 [ring-basic-authentication "1.0.5"]
                 [proto-repl "0.3.1"]
                 [com.outpace/config "0.10.0"]
                 [webjure/json-schema "0.7.4"]
                 [crypto-password "0.2.0"]
                 [com.draines/postal "2.0.2"]
                 [org.clojure/data.json "0.2.6"]]

  :exclusions [org.slf4j/slf4j-simple]

  :profiles {:uberjar {:aot :all}}

  :aliases {"config" ["run" "-m" "outpace.config.generate"]}

  :main lambdacd-pipeline.core)
