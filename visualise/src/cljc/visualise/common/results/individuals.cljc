(ns visualise.common.results.individuals
  (:require [clojure.string :as str]))

(def individual-keys [:name :dob :address])

(def individual-group-fn #(select-keys % individual-keys))

(def surname #(last (str/split (:name %) #" ")))

(defn group-by-individual [events]
  (group-by :ikey (map (fn [m] (assoc m :ikey (individual-group-fn m))) events)))

(defn individuals [events]
  (map-indexed
    (fn [idx m]
      (merge m {:idx   idx
                :ikey  (select-keys m individual-keys)}))
    (vec (sort-by surname (set (map #(select-keys % individual-keys) events))))))