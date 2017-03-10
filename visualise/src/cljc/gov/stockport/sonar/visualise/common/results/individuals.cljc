(ns gov.stockport.sonar.visualise.common.results.individuals
  (:require [clojure.string :as str]
            [gov.stockport.sonar.visualise.common.ui.colors :as c]))

(defn individual-color [number-of-individuals]
  (if (> number-of-individuals (count c/color-priority))
    (fn [_] :black)
    (fn [idx] (c/color idx))))

(def individual-keys [:name :dob :address :postcode])

(def individual-group-fn #(select-keys % individual-keys))

(def surname #(last (str/split (:name %) #" ")))

(defn group-by-individual [events]
  (group-by :ikey (map (fn [m] (assoc m :ikey (individual-group-fn m))) events)))

(defn individuals-with-scores [events]
  (map
    (fn [[ikey data :as m]]
      (merge
        {;
         :ikey  ikey
         :score (apply max (map :score data))}
        (select-keys ikey individual-keys)))
    (group-by-individual events)))

(defn individuals [events]
  (let [individuals-with-scores (individuals-with-scores events)
        color (individual-color (count individuals-with-scores))]
    (map-indexed
      (fn [idx m]
        (assoc m :idx idx :color (color idx)))
      (reverse (sort-by :score individuals-with-scores)))))

