(ns gov.stockport.sonar.visualise.util.date
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cljs-time.coerce :as c]
            [clojure.string :as str]))

(defn now []
  (t/now))

(defn parse
  [s formatters]
  (first
    (for [f formatters
          :let [d (try (f/parse f s) (catch :default _))]
          :when d] d)))

(defn parse-timestamp [timestamp]
  (when (not (str/blank? timestamp))
    (f/parse (:date-time f/formatters) timestamp)))

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

(defn human-since [date]
  (if date
    (let [interval (t/interval (t/at-midnight date) (t/at-midnight (t/now)))
          days (t/in-days interval)]
      (cond
        (> days 7) (str (t/in-weeks interval) " weeks ago")
        (= days 0) "Today"
        (= days 1) "Yesterday"
        (= days 7) "1 week ago"
        (> days 1) (str days " days ago")
        :else "dunno"))
    "-"))

(defn date-format [date]
  (f/unparse (f/formatter "d MMM yyyy") date))