(ns ingest.ingest-processed-csv
  (:require [clojure-csv.core :as cs]
            [ingest.client.elastic-search-client :as esc]
            [clj-time.format :as f]))

(defn raw-events [file]
  (let [csv (cs/parse-csv (slurp file))
        keys (map keyword (filter not-empty (first csv)))]
    (map #(zipmap keys (filter (fn [x] (not (nil? x))) %)) (rest csv))))

(defn date-fmt [field format]
  (fn [event]
    (assoc event field (f/unparse (format f/formatters) (f/parse (field event))))))

(def clean-dob (date-fmt :dob :date))
(def clean-ts (date-fmt :timestamp :date-time))

(defn ingest [file]
  (map
    #(-> %
         clean-dob
         clean-ts)
    (raw-events file)))

(defn upload [file]
  (esc/bulk-index (ingest file)))

