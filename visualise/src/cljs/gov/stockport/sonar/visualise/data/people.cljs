(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.util.popper :as popper]
            [clojure.string :as str]))

(def group-keys [:name :dob :address :postcode])

(defn by-people [data]
  {:people (reduce merge {}
                   (map (fn [[k v]] {k {:data v}})
                        (group-by (fn [m] (select-keys m group-keys)) data)))})

(defn with-max-score [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :score (apply max (map :score (:data v))))}) people))))

(def surname #(last (str/split (:name %) #" ")))

(defn with-rank [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map-indexed
                        (fn [idx [k v]] {k (assoc v :rank (+ 1 idx))})
                        (sort-by (fn [[_ p]] [(- 0 (:score p)) (surname p)]) people)))))

(defn display-all [{:keys [display-all? people] :as data}]
  (let [turning-all-on? (not display-all?)
        sufficient-colors? (>= (count c/colour-priority) (count people))
        available-colors (if (and turning-all-on? sufficient-colors?) c/colour-priority [])
        color-stack (popper/poppable available-colors :value-when-empty :black)]
    (-> data
        (assoc :display-all? turning-all-on?)
        (update :people
                (fn [people]
                  (reduce merge {}
                          (map
                            (fn [[k v]]
                              (let [next-color (color-stack)]
                                {k (assoc v :display turning-all-on? :color next-color)}))
                            people)))))))

(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (display-all)))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

