(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [clojure.string :as str]))

(def group-keys [:name :dob :address :postcode])

(defn by-people [data]
  (reduce merge {}
          (map (fn [[k v]] {k {:data v}})
               (group-by (fn [m] (select-keys m group-keys)) data))))

(defn with-max-score [people]
  (reduce merge {}
          (map (fn [[k v]] {k (assoc v :score (apply max (map :score (:data v))))}) people)))

(def surname #(last (str/split (:name %) #" ")))

(defn with-rank [people]
  (reduce merge {}
          (map-indexed
            (fn [idx [k v]] {k (assoc v :rank (+ 1 idx))})
            (sort-by (fn [[_ p]] [(- 0 (:score p)) (surname p)]) people))))

(defn with-all-shown [people]
  (reduce merge {}
          (map (fn [[k v]] {k (assoc v :display true)}) people)))

(defn with-colour-coding [people]
  (let [colour-fn (c/colour-for people)]
    (reduce merge {}
            (map (fn [[k v]] {k (assoc v :color (colour-fn (- (:rank v) 1)))}) people))))

(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (with-all-shown)
      (with-colour-coding)))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

(defn focus-on [data pkey]
  (update data :people (fn [people]
                         (reduce merge {}
                                 (map (fn [[k v]] {k (assoc v :display (= k pkey))}) people)))))

