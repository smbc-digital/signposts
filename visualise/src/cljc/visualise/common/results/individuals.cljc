(ns visualise.common.results.individuals
  (:require [clojure.string :as str]
            [visualise.common.ui.colors :as c]))

(defn individual-color [number-of-individuals]
  (if (> number-of-individuals (count c/color-priority))
    (fn [_] :black)
    (fn [idx] (c/color idx))))

(def individual-keys [:name :dob :address :postcode])

(def individual-group-fn #(select-keys % individual-keys))

(def surname #(last (str/split (:name %) #" ")))

(defn group-by-individual [events]
  (group-by :ikey (map (fn [m] (assoc m :ikey (individual-group-fn m))) events)))

(defn individuals [events]
  (let [individuals (vec (sort-by surname (set (map #(select-keys % individual-keys) events))))
        color (individual-color (count individuals))]
    (map-indexed
      (fn [idx m]
        (merge m {:idx   idx
                  :color (color idx)
                  :ikey  (select-keys m individual-keys)}))
      individuals)))

