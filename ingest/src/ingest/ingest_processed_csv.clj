(ns ingest.ingest-processed-csv
  (:require [clojure-csv.core :as cs]
            [ingest.client.elastic-search-client :as esc]
            [clj-time.format :as f]
            [clojure.java.io :as io]))


(defn raw-events [file]
  (let [csv (cs/parse-csv (io/reader file))
        keys (map keyword (filter not-empty (first csv)))]
    (map #(zipmap keys (filter (fn [x] (not (nil? x))) %)) (rest csv))))

(def input-date-format (f/formatter "dd/MM/yyyy"))

(defn date-fmt [field format]
  (fn [event]
    (if (field event)
    (assoc event field (f/unparse format (f/parse input-date-format (field event))))
    event
    )))

(def clean-dob (date-fmt :dob (:date f/formatters)))
(def clean-ts (date-fmt :timestamp (:date-time f/formatters)))

(defn ingest [file]
  (map
    #(-> %
         clean-dob
         clean-ts
         )
    (raw-events file)))

(defn upload [file]
  (esc/bulk-index (ingest file)))

