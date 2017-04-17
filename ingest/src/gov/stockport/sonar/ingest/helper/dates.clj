(ns gov.stockport.sonar.ingest.helper.dates
  (:require [clj-time.format :as f]
            [gov.stockport.sonar.ingest.helper.misc :refer [quietly]]))

(def not-nil? (complement nil?))

; order is important and tested
(def formatters
  (concat
    [(f/formatter "dd/MM/yy")
     (f/formatter "dd-MM-yy")
     (f/formatter "dd-MMM-yy")]
    (vals f/formatters)))

(defn format-for [str]
  (first
    (filter
      not-nil?
      (map
        (fn [fmt]
          (if (not-nil? (try (f/parse fmt str) (catch Exception _ nil))) fmt))
        formatters))))

; remembers the last 5 formats that worked with the most-recently working ones first
; is more efficient than f/parse when you can't anticipate the format in advance
(defn parser []
  (let [fmts (atom [])
        attempt (fn [val] (first (for [f @fmts :let [d (try (f/parse f val) (catch Exception _ nil))] :when d] d)))]
    (fn [val]
      (when (not-empty val)
        (or (attempt val)
            (when-let [fmt (format-for val)]
              (swap! fmts #(take 5 (cons fmt %)))
              (attempt val)))))))

(def parse (parser))

(def iso-format (:date-time f/formatters))

(def ymd-format (f/formatter "yyyy-MM-dd"))

(defn date->iso-date-string [date]
  (quietly (f/unparse iso-format date)))

(defn date->ymd-date-string [date]
  (quietly (f/unparse ymd-format date)))

(defn iso-date-string->date [string]
  (quietly (f/parse iso-format string)))

(defn iso-date-string? [str]
  (not-nil? (iso-date-string->date str)))

(defn date-string? [str]
  (not-nil? (parse str)))


;; Added by Jim to make things work, sure merge hell will emerge:

(def dmy-format (f/formatter "dd/MM/yyyy"))

(defn date->iso-date-string [date]
  (quietly (f/unparse iso-format date)))

(defn dmy-date-string->date [string]
  (quietly (f/parse dmy-format string)))

(defn dmy-date-string? [str]
  (not (nil? (dmy-date-string->date str))))