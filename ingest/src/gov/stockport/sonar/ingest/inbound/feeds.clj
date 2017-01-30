(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [clojure-csv.core :as csv])
  (:import (java.io File)))

(defn- path-to [directory]
  (str (:inbound-dir @!config) "/" directory))

(defn hash [^File file]
  (files/mtime file)) ; for now

(defn csv-stream [file]
  (let [rdr (files/open-reader file)]
    {:csv      (csv/parse-csv rdr)
     :close-fn #(files/close-reader rdr)}))

(defn apply-csv-processing [file csv-processor]
  (let [{:keys [csv close-fn]} (csv-stream file)]
    (try
      (csv-processor {:name      (files/name file)
                      :feed-hash (hash file)
                      :csv       csv})
      (finally (close-fn)))))

(defn process-feed-file [file csv-processor]
  (try
    (let [result (apply-csv-processing file csv-processor)]
      (if (:failed result)
        (files/move-file file (path-to "failed"))
        (files/move-file file (path-to "processed")))
      result)
    (catch Exception e
      (log e)
      (files/move-file file (path-to "failed")))))

(defn process-feeds [csv-processor]
  (filter
    (complement nil?)
    (map #(process-feed-file % csv-processor) (files/list-files (path-to "ready")))))