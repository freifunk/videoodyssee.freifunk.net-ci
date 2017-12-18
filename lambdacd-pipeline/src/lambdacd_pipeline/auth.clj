(ns lambdacd-pipeline.auth
  (:require [crypto.password.bcrypt :as bcrypt]
            [clojure.data.codec.base64 :as base64]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]))

(def DEFAULT_FILE "videoodyssee.user")

(defn- byte-transform
  "Returns the transformed bytes of the given bytes data."
  [direction-fn data]
  (reduce str (map char (direction-fn data))))

(defn- base64-encode-bytes
  "Returns the base-64 encoded string of the given bytes data."
  [data]
  (byte-transform base64/encode data))

(defn- base64-decode-bytes
  "Returns the base-64 decoded string of the given bytes data."
  [data]
  (byte-transform base64/decode data))

(defn- base64-encode
  [s]
  (base64-encode-bytes (.getBytes s)))

(defn- base64-decode
  [s]
  (base64-decode-bytes (.getBytes s)))

(defn user
  "Returns the logged in username by extracting from the HTTP request header."
  [req]
  (let [auth ((:headers req) "authorization")
        cred (and auth
                  (base64-decode
                   (last
                    (re-find #"^Basic (.*)$" auth))))
        user (and cred
                  (last
                   (re-find #"^(.*):" cred)))]
    user))

(defn read-users
  "Returns a map of user password read from the given file or default file videoodyssee.user."
  ([] (read-users DEFAULT_FILE))
  ([file] (if (.exists (io/file file))
            (reduce #(apply (partial assoc %) (str/split %2 #":")) {}
                    (str/split-lines (slurp file)))
            (
              assoc {} "admin" "admin"
              ))))

(defn sha1
  "Returns sha1 of the given string s."
  [s]
  (.digest (java.security.MessageDigest/getInstance "SHA1") (.getBytes s)))

(defn is-valid-user?
  "Returns true if the user and password is correct."
  [user password]
  (let [users (read-users)]
    (if (users user) (cond (str/starts-with? (users user) "{SHA}") (= (users user) (str "{SHA}" (base64-encode-bytes (sha1 password))))
      (str/starts-with? (users user) "$2a$") (bcrypt/check password (users user))
      (str/starts-with? (users user) "$2y$") (bcrypt/check password (str/replace (users user) "$2y$""$2a$"))
      :else (= (users user) password))
      false)
    ))
