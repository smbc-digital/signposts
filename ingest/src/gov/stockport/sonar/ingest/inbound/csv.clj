(ns gov.stockport.sonar.ingest.inbound.csv
  (:require [clojure-csv.core :as cs]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [clojure.string :as str]))

(def split (fn [val] (first (cs/parse-csv val))))
(def contains-comma? (fn [val] (str/includes? val ",")))

(defn mapper [headers]

  (let [headers (split headers)]

    (when (some empty? headers)
      (throw (IllegalStateException. "Blank values not allowed in file header")))

    (when (some contains-comma? headers)
      (throw (IllegalStateException. "Comma delimited headings not allowed in file header")))

    (let [header-keys (map (comp ->kebab-case keyword) headers)
          expected-data-count (count header-keys)]

      (fn mapper-fn [idx data]

        (let [data (split data)]

          (if (= (count data) expected-data-count)

            {:idx  idx
             :data (zipmap header-keys data)}

            {:idx   idx
             :error :insufficient-data
             :data  {}}))))))
