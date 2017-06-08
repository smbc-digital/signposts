(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]))

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

(defn process-feed [file]
  (process-with-buffer file (buffer/create-buffer
                              {:capacity (:batch-size @!config) :flush-fn flusher/flush-events})))

(defn process-feed-file [{:keys [file file-name]}]
  (try
    (log "Processing [" (files/fname file) "]")
    (let [result (process-feed file)]
      (if (:failed result)
        (files/write-failed-file file-name)
        (files/write-done-file file-name))
      result)
    (catch Exception e
      (log (.getMessage e))
      (files/write-failed-file file-name))))

(defn- unique [names]
(map (fn [[name _]] name) (filter (fn [[_ qty]] (= qty 1)) (frequencies names))))

(defn filter-for [base-file-names]
  (fn [{:keys [file-name]}]  (some #{(files/base-name file-name)} (unique base-file-names))))

(defn filter-csvs []
  (fn [{:keys [file-name]}] (= (files/extension file-name) ".csv")))

(defn get-csvs [dir-name]
  (let [all-files (files/list-files dir-name)
        base-file-names (map files/base-name (map :file-name all-files))]
    (filter
      (every-pred
        (filter-csvs)
        (filter-for base-file-names))
      all-files)))

(defn process-feeds []
  (filter
    (complement nil?)
    (map process-feed-file (get-csvs (:inbound-dir @!config)))))