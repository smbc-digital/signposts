(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]))

(def group-keys [:name :dob :address :postcode])

(defn by-people [data]
  (reduce merge {}
          (map (fn [[k v]] {k {:data v}})
               (group-by (fn [m] (select-keys m group-keys)) data))))

(defn with-max-score [people]
  (reduce merge {}
          (map (fn [[k v]] {k (assoc v :score (apply max (map :score (:data v))))}) people)))

(defn with-rank [people]
  (reduce merge {}
          (map-indexed
            (fn [idx [k v]] {k (assoc v :rank (+ 1 idx))})
            (sort-by (fn [[_ p]] [(- 0 (:score p)) (:name p)]) people))))

(defn with-colour-coding [people]
  (let [colour-fn (c/colour-for people)]
    (reduce merge {}
            (map (fn [[k v]] {k (assoc v :color (colour-fn (- (:rank v) 1)))}) people))))

(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (with-colour-coding)))