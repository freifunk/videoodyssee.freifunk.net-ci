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

    (alias "Processing Video"
           (with-workspace
            (alias "git"
                   (run
                    clone))
            (alias "Fix Metadata"
                   (run fix-metadata))
  ))))
