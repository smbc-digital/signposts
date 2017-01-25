(ns gov.stockport.sonar.ingest.inbound-data.csv-reader
  (:require [clojure-csv.core :as cs]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]))

(defn read-csv [{:keys [file]}]
  (log "processing file [" (fs/base-name file) "]")
  (let [reader (io/reader file)]
  {:file     file
   :reader   reader
   :csv-data (cs/parse-csv reader)}))