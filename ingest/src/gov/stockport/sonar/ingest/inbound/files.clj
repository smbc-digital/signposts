(ns gov.stockport.sonar.ingest.inbound.files
  (:require [clojure.java.io :as io]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [clojure.string :as str])
  (:import (java.io File Writer)))

(defn mtime [^File file]
  (.lastModified file))

(defn- older [^File f1 ^File f2]
  (< (mtime f1) (mtime f2)))

(defn fname [^File file]
  (.getName file))

(defn base-name [file-name]
  (subs file-name 0 (or (str/last-index-of file-name ".") (count file-name))))

(defn extension [file-name]
  (subs file-name (or (str/last-index-of file-name ".") (count file-name))))

(defn list-files [dir-name]
  (map
    (fn [file]
      {:file-name (.getName ^File file)
       :file      file})
    (sort older (.listFiles (io/file dir-name)))))

(defn open-reader [^File file]
  (io/reader file))

(defn- write-file [file-name]
  (let [file-name-and-path (str (:inbound-dir @!config) "/" file-name)]
    (with-open [w (clojure.java.io/writer file-name-and-path :append true)]
      (.write ^Writer w file-name))))

(defn write-done-file [file-name]
  (write-file (str (base-name file-name) ".done")))

(defn write-failed-file [file-name]
  (write-file (str (base-name file-name) ".failed")))