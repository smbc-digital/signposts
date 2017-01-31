(ns gov.stockport.sonar.ingest.inbound.feeds
  (:require [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [clojure-csv.core :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [gov.stockport.sonar.ingest.clock :as clock])
  (:import (java.io File)))

(defn- path-to [directory]
  (str (:inbound-dir @!config) "/" directory))

(defn fhash [^File file]
  (files/mtime file))                                       ; for now

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

(defn first-line-of [file]
  (with-open [rdr (files/open-reader file)]
    (first (line-seq rdr))))

(defn remaining-lines-of [rdr]
  (rest (line-seq rdr)))

(defn headers [keys]
  (map #(keyword (str 'gov.stockport.sonar.spec.event-spec) (str/trim %)) (filter not-empty keys)))

(defn empty-event-buffer [hash]
  {:qty 0 :events [] :feed-hash hash})

(defn flush-event-buffer [!buffer]
  (let [{:keys [feed-hash events]} @!buffer]
    (esc/bulk-index-list (fn [_] (str "events-" feed-hash)) events)
    (reset! !buffer (empty-event-buffer feed-hash))))

(defn event-buffer [capacity hash]
  (let [!buffer (atom (empty-event-buffer hash))]
    (fn [event]
      (if (= :flush event)
        (flush-event-buffer !buffer)

        (let [qty (:qty (swap! !buffer #(-> %
                                            (update :qty inc)
                                            (update :events (fn [e] (cons event e))))))]
          (if (= capacity qty) (flush-event-buffer !buffer)))))))


(defn inverted []
  (doseq [file (files/list-files (path-to "ready"))]
    (let [header-keys  (headers (first (csv/parse-csv (first-line-of file))))
          event-buffer (event-buffer 10000 (clock/now-millis))]
      (println "processing file [" (files/name file) "]")
      (with-open [rdr (files/open-reader file)]
        (doseq [data (remaining-lines-of rdr)]
          (event-buffer (zipmap header-keys (first (csv/parse-csv data))))))
      (event-buffer :flush))))