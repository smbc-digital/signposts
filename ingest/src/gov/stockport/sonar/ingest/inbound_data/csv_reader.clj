(ns gov.stockport.sonar.ingest.inbound-data.csv-reader
  (:require [clojure-csv.core :as cs]
            [clojure.java.io :as io]))

(defn read-csv [file]
  {:file     file
   :csv-data (cs/parse-csv (io/reader file))})


