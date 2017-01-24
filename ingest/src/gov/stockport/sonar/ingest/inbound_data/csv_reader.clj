(ns gov.stockport.sonar.ingest.inbound-data.csv-reader
  (:require [clojure-csv.core :as cs]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]))

(defn read-csv [file]
  (log "processing file [" (fs/base-name file) "]")
  {:file     file
   :csv-data (cs/parse-csv (io/reader file))})