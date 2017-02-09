(ns visualise.util.date
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]))

(defn age [dob]
  (try
    (t/in-years (t/interval (f/parse dob) (t/now)))
    (catch js/Error e "UNK")))
