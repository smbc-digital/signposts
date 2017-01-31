(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv])
  (:import (java.io File)))

(defn- path-to [directory]
  (str (:inbound-dir @!config) "/" directory))

(defn fhash [^File file]
  (files/mtime file))                                       ; for now

;(defn csv-stream [file]
;  (let [rdr (files/open-reader file)]
;    {:csv      (csv/parse-csv rdr)
;     :close-fn #(files/close-reader rdr)}))
;
;(defn apply-csv-processing [file csv-processor]
;  (let [{:keys [csv close-fn]} (csv-stream file)]
;    (try
;      (csv-processor {:name      (files/fname file)
;                      :feed-hash (fhash file)
;                      :csv       csv})
;      (finally (close-fn)))))

(defn first-line-of [file]
  (with-open [rdr (files/open-reader file)]
    (first (line-seq rdr))))

(defn remaining-lines-of [rdr]
  (rest (line-seq rdr)))

(def line-numberer (fn [k v] [(+ 2 k) v]))

(defn feed-processor [{:keys [queue flush]} file]
  (let [csv-mapper (csv/mapper (first-line-of file))]
    (with-open [rdr (files/open-reader file)]
      (doseq [[line-number data] (map-indexed line-numberer (remaining-lines-of rdr))]
        (queue (csv-mapper line-number data)))
      (flush))))

(defn process-feed-file [file feed-processor]
  (try
    (log "Processing [" (files/fname file) "]")
    (let [result (feed-processor file)]
      (if (:failed result)
        (files/move-file file (path-to "failed"))
        (files/move-file file (path-to "processed")))
      result)
    (catch Exception e
      (log e)
      (files/move-file file (path-to "failed")))))

(defn process-feeds [feed-processor]
  (filter
    (complement nil?)
    (map #(process-feed-file % feed-processor) (files/list-files (path-to "ready")))))