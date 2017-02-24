(ns visualise.common.results.individuals
  (:require [clojure.string :as str]))

(def surname #(last (str/split (:name %) #" ")))

(defn individuals [events]
  (vec (sort-by surname (set (map (fn [x] (select-keys x [:name :dob :address])) events)))))
