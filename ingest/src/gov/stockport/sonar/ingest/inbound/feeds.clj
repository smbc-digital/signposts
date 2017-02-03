(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [pandect.algo.sha1 :refer :all]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]))

(defn- path-to [directory]
  (str (:inbound-dir @!config) "/" directory))

(defn first-line-of [file]
  (with-open [rdr (files/open-reader file)]
    (first (line-seq rdr))))

(defn remaining-lines-of [rdr]
  (rest (line-seq rdr)))

(def line-numberer (fn [k v] [(+ 2 k) v]))

(defn process-with-buffer [file {:keys [queue flush]}]
  (let [csv-mapper (csv/mapper (first-line-of file))]
    (with-open [rdr (files/open-reader file)]
      (doseq [[line-number data] (map-indexed line-numberer (remaining-lines-of rdr))]
        (queue (csv-mapper line-number data)))
      (flush))))

(defn feed-hash [file]
  (sha1 (files/fname file)))

(defn process-feed [file]
  (process-with-buffer file (buffer/create-buffer
                              {:capacity (:batch-size @!config) :flush-fn flusher/flush-events :feed-hash (feed-hash file)})))

(defn process-feed-file [file]
  (try
    (log "Processing [" (files/fname file) "]")
    (let [result (process-feed file)]
      (if (:failed result)
        (files/move-file file (path-to "failed"))
        (files/move-file file (path-to "processed")))
      result)
    (catch Exception e
      (log (.getMessage e))
      (files/move-file file (path-to "failed")))))

(defn process-feeds []
  (filter
    (complement nil?)
    (map process-feed-file (files/list-files (path-to "ready")))))