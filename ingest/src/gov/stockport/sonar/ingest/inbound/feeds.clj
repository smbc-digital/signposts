(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [clojure.string :as str]))

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

(defn process-feed [file]
  (process-with-buffer file (buffer/create-buffer
                              {:capacity (:batch-size @!config) :flush-fn flusher/flush-events})))

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

(defn- base-name [{:keys [file-name]}]
  (subs file-name 0 (str/last-index-of file-name ".")))

(defn- extension [{:keys [file-name]}]
  (subs file-name (str/last-index-of file-name ".")))

(defn- unique [names]
(map (fn [[name _]] name) (filter (fn [[_ qty]] (= qty 1)) (frequencies names))))

(defn filter-for [base-file-names]
  (fn [file]  (some #{(base-name file)} (unique base-file-names))))

(defn filter-csvs []
  (fn [file] (= (extension file) ".csv")))

(defn get-csvs [dir-name]
  (let [all-files (files/list-wrapped-files dir-name)
        base-file-names (map base-name all-files)]
    (filter
      (every-pred
        (filter-csvs)
        (filter-for base-file-names))
      all-files)))

(defn process-feeds []
  (filter
    (complement nil?)
    (map process-feed-file (files/list-files (path-to "ready")))))