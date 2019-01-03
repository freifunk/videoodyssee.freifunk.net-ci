(ns lambdacd-pipeline.steps
  (:require
   [lambdacd.steps.shell :as shell]
   [lambdacd-pipeline.utils :as utils]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [org.httpkit.client :as httpclient]
   [outpace.config :refer [defconfig]]
   [postal.core :as mail]
   [clojure.tools.logging :as log])
  )

(defconfig ^:required scripts-path "/opt/pipeline")
(defconfig ^:required cdn-url "rsync://yourpersonalexample.net:/module")
(defconfig ^:required api-url "https://your-api-url")
(defconfig ^:required api-key "your-api-key")
(defconfig ^:required admin-recipients "admin-email-address")
(defconfig ^:required sender-address "sender@example.org")

(def video-base-path "/srv/videoodyssee/")

(defn publish-format [args ctx format length filename]
  (let [cwd (:cwd args)]
    @(httpclient/post (str api-url "/api/recordings")
      {:body (json/json-str {
                              :api_key api-key,
                              :guid (utils/get-uuid args),
                              :recording {
                                           :filename (str (utils/get-basename-without-extension (utils/get-param args "videoFilePath")) "." format),
                                           :folder (utils/get-uuid args),
                                           :mime_type (str "video/" format),
                                           :language (utils/get-param args "language"),
                                           :size (utils/file-size-in-mb (str filename "." format)),
                                           :length length,
                                           :width (utils/ffmpeg-get-width cwd ctx (str filename "." format)),
                                           :height (utils/ffmpeg-get-height cwd ctx (str filename "." format))
                                           }
                              } :escape-slash false )
       :headers {"Content-Type" "application/json"}})
    )
  )

;;
;; Steps
;;

(defn fix-metadata [args ctx]
  (let [cwd (:cwd args)]
    (log/info (str "uuid: " (utils/get-uuid args)))
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/fixed-metadata"))
    (shell/bash ctx scripts-path
                (str "sh scripts/fix-metadata.sh \"" (utils/get-param args "videoFilePath") "\" " video-path "/fixed-metadata"))))

(defn encode-wbem [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to webm")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_webm.sh " video-path "/fixed-metadata "
                     \"(utils/get-param args "videoFilePath")\" " "
                     video-path "/processed-video"))))

(defn encode-h264 [args ctx]
  (let [cwd (:cwd args)]

    (log/info "encode video to h264")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str "sh scripts/encode_h264_AAC_HQ.sh " video-path "/fixed-metadata "
                     \"(utils/get-param args "videoFilePath")\" " "
                     video-path "/processed-video "
                     (utils/get-param args "title")))))

(defn send-message-to-admins [args ctx]
  (mail/send-message {
                       :from sender-address
                       :to admin-recipients
                       :subject "New video ready to be published"
                       :body (str "Hi! There's a new video waiting to be published. "
                            "The title is \"" (utils/get-param args "title") "\"."
                                  "Submitted by " (utils/get-param args "name ") " email: " (utils/get-param args "email"))
                       })
  {:status :success}
  )

(defn send-message-to-submitter [args ctx]
  (mail/send-message {
                       :from sender-address
                       :to (utils/get-param args "email")
                       :subject "Your video is published"
                       :body (str "Hi "(utils/get-param args "name ") "! Your video is now published at https://media.freifunk.net. "
                                  "The title is \"" (utils/get-param args "title") "\".")
                       })
  {:status :success}
  )

(defn upload-to-cdn [args ctx]
  (let [cwd (:cwd args)]

    (def video-path (str video-base-path (utils/get-uuid args)))
    (def conferenceAcronym (utils/get-param args "conferenceAcronym"))

       (shell/bash ctx scripts-path
                (str "sh scripts/upload_video_to_cdn.sh " video-path " "
                     cdn-url " "
                     (utils/get-uuid args) " "
                     conferenceAcronym))
    ))

(defn upload-to-youtube [args ctx]
  (let [cwd (:cwd args)]
    (def video-path (str video-base-path (utils/get-uuid args)))
    (log/info "upload to youtube")
    (shell/bash ctx scripts-path (str "python2 scripts/upload_youtube_video.py "
                                      "--file \"" video-path "/processed-video/" (utils/get-basename-without-extension (utils/get-param args "videoFilePath")) ".mp4\" "
                                      "--title \"" (utils/get-param args "title") "\" "
                                      "--description \"" (utils/get-param args "description") "\" "
                                      "--keywords \"" (clojure.string/join ","(utils/get-param args "tags")) "\" "
                                      "--category \"27\" " ;; Education
                                      "--privacyStatus \"private\""))
    ))

(defn publish-event [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to voctoweb")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (def recordings {})
    (def created-event @(httpclient/post (str api-url "/api/events")
     {:body (json/json-str {
                             :api_key api-key,
                             :acronym (utils/get-param args "conferenceAcronym"),
                             :event {
                                      :poster_filename (str (utils/get-uuid args)"/" (utils/get-basename-without-extension (utils/get-param args "videoFilePath")) "_preview.jpg"),
                                      :thumb_filename (str (utils/get-uuid args)"/" (utils/get-basename-without-extension (utils/get-param args "videoFilePath")) "_thumb.jpg"),
                                      :guid (utils/get-uuid args),
                                      :slug (utils/get-param args "slug"),
                                      :title (utils/get-param args "title"),
                                      :subtitle (utils/get-param args "subtitle"),
                                      :persons (utils/get-param args "persons"),
                                      :tags (utils/get-param args "tags"),
                                      :date (utils/get-param args "date"),
                                      :description (utils/get-param args "description"),
                                      :link (utils/get-param args "link"),
                                      :release_date (utils/get-param args "releaseDate"),
                                      :original_language (utils/get-param args "language")
                             }} :escape-slash false)
      :headers {"Content-Type" "application/json"}}))

    (if (>= (get created-event :status) 300)
      {:status :failure :out (str "Result create event - Status:"(get created-event :status) ", " (get created-event :body))}
      {:status :success}
      )
    ))

(defn publish-recordings [args ctx]
  (let [cwd (:cwd args)]
    (def length (utils/ffmpeg-get-length cwd ctx (str video-path "/processed-video/" (utils/get-basename-without-extension (utils/get-param args "videoFilePath")) ".mp4")))
    (def filename (str video-path "/processed-video/" (utils/get-basename-without-extension (utils/get-param args "videoFilePath"))))
    (def webm-result (publish-format args ctx "webm" length filename))
    (def mp4-result (publish-format args ctx "mp4" length filename))
    (if (and (< (get webm-result :status) 300) (< (get mp4-result :status) 300) )
      {:status :success}
      {:status :failure :out (str "Result create webm - Status:"(get webm-result :status) ", " (get webm-result :body)
                                  " ---- Result create mp4 - Status:"(get mp4-result :status) ", " (get mp4-result :body))}
      )

  ))

(defn publish-to-socialmedia [args ctx]
  (let [cwd (:cwd args)]

    (log/info "publish to socialmedia")
    (shell/bash ctx cwd "exit 0")
    ))

(defn create-thumbnail-images [args ctx]
  (let [cwd (:cwd args)]

    (log/info "create thumbnail images")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "mkdir -p " video-path "/processed-video/"))
    (shell/bash ctx scripts-path
                (str"sh scripts/create_poster_thumbnails.sh " video-path "/fixed-metadata "
                    \"(utils/get-param args "videoFilePath")\" " "
                    video-path "/processed-video"))
    ))

(defn cleanup [args ctx]
  (let [cwd (:cwd args)]

    (log/info "cleanup")
    (def video-path (str video-base-path (utils/get-uuid args)))
    (shell/bash ctx cwd (str "rm -r " video-path))
    ))