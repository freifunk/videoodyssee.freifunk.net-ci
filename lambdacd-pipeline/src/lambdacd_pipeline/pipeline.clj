(ns lambdacd-pipeline.pipeline
  (:use [lambdacd-pipeline.steps])
  (:require
    [lambdacd.steps.control-flow :refer :all]
    [lambdacd.steps.manualtrigger :as manualtrigger]
    [lambdacd-pipeline.trigger :as trigger])
  (:refer-clojure :exclude
                  [alias]))


(def pipeline-def
  `((alias
     "triggers"
     trigger/wait-for-external-trigger)

    (alias
     "Processing Video"
     (with-workspace
      (alias "Fix Metadata"
             (run fix-metadata))
      (in-parallel
       (alias "Encode H264"
              (run encode-h264))
       (alias "Create thumbnails"
              (run create-thumbnail-images))
       (alias "Create timelens data"
              (run create-timelens-data)))
       (alias "Encode WBEM"
              (run encode-wbem))))
    (alias "send mail to admins" send-message-to-admins)

    (alias
     "Publishing Video"
     (with-workspace
       (alias "Confirm publishing"
              ;;manualtrigger/wait-for-manual-trigger
              )
      (in-parallel
        (alias "Upload to CDN"
               (run upload-to-cdn))
        (alias "Upload to YouTube"
               ;;     (run upload-to-youtube)
               ))
       (alias "Publish VocToWeb"
              (run publish-event
                               publish-recordings))
       (alias "Publish to Social Media"
              (run publish-to-socialmedia))
       (alias "send mail to submitter"
              (run send-message-to-submitter))
       (alias "Cleanup processed files"
             (run cleanup))))))
