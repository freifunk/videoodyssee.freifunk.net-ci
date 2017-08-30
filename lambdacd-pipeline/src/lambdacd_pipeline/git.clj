(ns lambdacd-pipeline.git
    (:require
        [clojure.tools.logging :as log]
        [clojure.java.io :as io]
        [clojure.string :as str])

    (:import (org.eclipse.jgit.api Git)
             (org.eclipse.jgit.lib Ref TextProgressMonitor)
             (org.eclipse.jgit.revwalk RevCommit RevWalk)
             (java.util Date)))

;; copied from lambdacd-git.git
(defn- ^Git git-open [workspace]
    "http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/api/Git.html"
    (Git/open (io/file workspace)))

(defn current-commit-hash [workspace]
    (let [git          (git-open workspace)

          ;; http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/api/LogCommand.html
          logs         (-> (.log git)
                           (.setMaxCount 1)
                           (.call))

          commit-hash  (-> (first logs)
                           (.getId)
                           (.getName))]
        commit-hash))
