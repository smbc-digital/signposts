(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [pandect.algo.md5 :as md5]))

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

(defn- matching-done-file? [csv-file done-file]
  (= (md5/md5-file csv-file)
     (slurp done-file)))

(defn should-process-feed-file [file-name]
  (let [no-failed-file? (not (files/exists? (files/get-full-path (files/failed-file-name file-name))))
        done-file (files/get-full-path (files/done-file-name file-name))
        csv-file (files/get-full-path file-name)]
    (if no-failed-file?
      (or (not (files/exists? done-file))
          (not (matching-done-file? csv-file done-file))))))

(defn process-feed-file [{:keys [file file-name]}]
  (if (should-process-feed-file file-name)
    (try
      (log "Processing [" (files/fname file) "]")
      (let [result (process-feed file)]
        (if (:failed result)
          (files/write-failed-file file-name)
          (files/write-done-file file-name))
        result)
      (catch Exception e
        (log (.getMessage e))
        (files/write-failed-file file-name)))))

(defn filter-csvs [{:keys [file-name]}]
  (= (files/extension file-name) ".csv"))

(defn get-csvs [dir-name]
  (log (str "Processing csv files from [" dir-name "]"))
  (let [all-files (files/list-files dir-name)
        base-file-names
        (map files/base-name
             (map :file-name all-files))]
    (filter filter-csvs all-files)))

(defn process-feeds []
  (let [all-csvs (get-csvs (:inbound-dir @!config))]
    (filter
      (complement nil?)
      (map process-feed-file all-csvs))))