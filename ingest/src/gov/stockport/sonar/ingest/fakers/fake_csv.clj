(ns gov.stockport.sonar.ingest.fakers.fake-csv
  (:require [clojure-csv.core :as csv]))

(defn as-csv [events]
  (if (not-empty events)
    (let [header (into [] (map name (keys (first events))))
          data (map (fn [event] (into [] (map str (vals event)))) events)]
      (csv/write-csv (cons header data)))))