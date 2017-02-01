(ns gov.stockport.sonar.ingest.inbound.files
  (:require [clojure.java.io :as io]
            [gov.stockport.sonar.ingest.util.logging :refer [log]])
  (:import (java.io File)))

(defn mtime [^File file]
  (.lastModified file))

(defn- older [^File f1 ^File f2]
  (< (mtime f1) (mtime f2)))

(defn fname [^File file]
  (.getName file))

(defn list-files [dir-name]
  (log "looking in [" dir-name "]")
  (sort older (.listFiles (io/file dir-name))))

(defn move-file [^File from to-dir]
  (.renameTo from (io/file to-dir (.getName from))))

(defn open-reader [^File file]
  (io/reader file))