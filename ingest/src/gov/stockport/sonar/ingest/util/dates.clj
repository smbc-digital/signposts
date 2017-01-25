(ns gov.stockport.sonar.ingest.util.dates
  (:require [clj-time.format :as f]
            [gov.stockport.sonar.ingest.util.misc :refer [quietly]]))

(def iso-format (:date-time f/formatters))

(def dmy-format (f/formatter "dd/MM/yyyy"))

(defn date->iso-date-string [date]
  (quietly (f/unparse iso-format date)))

(defn iso-date-string->date [string]
  (quietly (f/parse iso-format string)))

(defn dmy-date-string->date [string]
  (quietly (f/parse dmy-format string)))

(defn iso-date-string? [str]
  (not (nil? (iso-date-string->date str))))

(defn dmy-date-string? [str]
  (not (nil? (dmy-date-string->date str))))