(ns visualise.util.date
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]))

(defn parse
  [s formatters]
  (first
    (for [f formatters
          :let [d (try (f/parse f s) (catch :default _))]
          :when d] d)))

(defn age [dob]
  (try
    (t/in-years (t/interval (parse dob [(f/formatter "dd/MM/yyyy")
                                        (f/formatter "dd-MM-yyyy")
                                        (f/formatter "yyyy-MM-dd")]) (t/now)))
    (catch js/Error e "UNK")))
