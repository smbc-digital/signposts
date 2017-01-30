(ns gov.stockport.sonar.ingest.fakers.fake-csv
  (:require [clojure-csv.core :as csv]
            [gov.stockport.sonar.ingest.util.dates :refer [date->iso-date-string]])
  (:import (org.joda.time DateTime)))

(def coerce
  (fn [val]
    (let [coercion (cond
                     (instance? DateTime val) date->iso-date-string
                     :else str)]
      (coercion val))))

(defn as-csv [events]
  (if (not-empty events)
    (let [header (into [] (map name (keys (first events))))
          data (map (fn [event] (into [] (map coerce (vals event)))) events)]
      (csv/write-csv (cons header data)))))