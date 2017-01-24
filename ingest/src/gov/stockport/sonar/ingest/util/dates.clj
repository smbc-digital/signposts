(ns gov.stockport.sonar.ingest.util.dates
  (:require [clj-time.core :as t]
            [clj-time.format :as f]))

(def iso-format (:date-time f/formatters))

(defmacro quietly [form]
  `(try
     ~form
     (catch Exception _# nil)))


(defn date->iso-date-string [date]
  (quietly (f/unparse iso-format date)))

(defn iso-date-string->date [string]
  (quietly (f/parse iso-format string)))

(defn iso-date-string? [str]
  (not (nil? (iso-date-string->date str))))