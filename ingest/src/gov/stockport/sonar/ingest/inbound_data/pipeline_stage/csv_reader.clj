(ns gov.stockport.sonar.ingest.inbound-data.pipeline-stage.csv-reader
  (:require [clojure-csv.core :as cs]))

(defn stream->csv [{:keys [stream] :as state}]
  (assoc state :csv-data (cs/parse-csv stream)))