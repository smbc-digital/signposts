(ns gov.stockport.sonar.ingest.util.dates
  (:require [clj-time.format :as f]
            [gov.stockport.sonar.ingest.util.misc :refer [quietly]]))

(def iso-format (:date-time f/formatters))

(defn date->iso-date-string [date]
  (quietly (f/unparse iso-format date)))

(defn iso-date-string->date [string]
  (quietly (f/parse iso-format string)))

(defn iso-date-string? [str]
  (not (nil? (iso-date-string->date str))))