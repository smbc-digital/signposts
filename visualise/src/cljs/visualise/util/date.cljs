(ns visualise.util.date
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cljs-time.coerce :as c]))

(defn parse
  [s formatters]
  (first
    (for [f formatters
          :let [d (try (f/parse f s) (catch :default _))]
          :when d] d)))

(defn parse-timestamp [timestamp]
  (f/parse (:date-time f/formatters) timestamp))

(defn as-millis [timestamp]
  (c/to-long timestamp))

(defn age [dob]
  (try
    (let [age (t/in-years (t/interval (parse dob
                                             [(f/formatter "dd/MM/yyyy")
                                              (f/formatter "dd-MM-yyyy")
                                              (f/formatter "dd-MMM-YY")
                                              (f/formatter "yyyy-MM-dd")]) (t/now)))]
      (if (> age 1000) (- age 1900) age))
    (catch js/Error e "UNK")))
