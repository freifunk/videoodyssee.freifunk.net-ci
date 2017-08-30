(ns lambdacd-pipeline.pipeline
  (:use [lambdacd.steps.control-flow]
        [lambdacd-pipeline.steps])
  (:require
   [lambdacd.steps.manualtrigger :as manualtrigger]
   [lambdacd-git.core :as lambdacd-git])
  (:refer-clojure :exclude
                  [alias]))

(def pipeline-def
  `((alias
     "triggers"
     (either
      manualtrigger/wait-for-manual-trigger
      wait-for-repo))


  (alias "Hello World"
         (run hello-world))
  ))
