(ns lambdacd-pipeline.pipeline
  (:use [lambdacd.steps.control-flow]
        [lambdacd-pipeline.steps])
  (:require
   [lambdacd.steps.manualtrigger :as manualtrigger]
   [lambdacd-pipeline.trigger :as trigger])
  (:refer-clojure :exclude
                  [alias]))


(def pipeline-def
  `((alias
     "triggers"
     (either
      manualtrigger/wait-for-manual-trigger
      trigger/wait-for-external-trigger
      ))

    (alias "Processing Video"
           (with-workspace
            (alias "git"
                   (run
                    clone))
            (alias "Fix Metadata"
                   (run fix-metadata))
            (in-parallel
             (alias "Encode WBEM"
                    (run encode-wbem))
             (alias "Encode H264"
                    (run encode-h264)))))
    (alias "Publishing Video"
           (with-workspace
            (alias "Confirm publishing"
                   manualtrigger/wait-for-manual-trigger)
            (in-parallel
             (alias "Upload to CDN"
                    (run upload-to-cdn))
             (alias "Upload to YouTube"
                    (run upload-to-youtube)))
            (in-parallel
             (alias "Publish to VocToWeb"
                    (run publish-to-voctoweb))
             (alias "Publish to Social Media"
                    (run publish-to-socialmedia)))
            (alias "Cleanup processed files"
                   (run cleanup))
  ))))
