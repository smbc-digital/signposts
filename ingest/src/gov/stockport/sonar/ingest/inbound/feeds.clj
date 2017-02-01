(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]))

(defn- path-to [directory]
  (str (:inbound-dir @!config) "/" directory))

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