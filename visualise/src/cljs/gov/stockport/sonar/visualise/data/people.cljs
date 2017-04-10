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


(defn toggle-collapse-all [{:keys [all-collapsed?] :as data}]
  (let [collapsing-all? (not all-collapsed?)]
    (-> data
        (assoc :all-collapsed? collapsing-all?)
        (update :people
                (fn [people]
                  (reduce merge {}
                          (map
                            (fn [[k v]]
                              {k (assoc v :collapsed? collapsing-all?)})
                            people)))))))


(defn toggle-display-all [{:keys [all-displayed? people] :as data}]
  (let [turning-all-on? (not all-displayed?)
        sufficient-colors? (>= (count c/colour-priority) (count people))
        available-colors (if (or (not turning-all-on?) (and turning-all-on? sufficient-colors?)) c/colour-priority [])
        color-stack (popper/poppable available-colors :value-when-empty :black)]
    (-> data
        (assoc :all-displayed? turning-all-on?)
        (assoc :color-stack color-stack)
        (update :people
                (fn [people]
                  (reduce merge {}
                          (map
                            (fn [[k v]]
                              (let [next-color (if turning-all-on? (color-stack) :black)]
                                {k (assoc v :displayed? turning-all-on? :color next-color)}))
                            people)))))))

(defn toggle-display-person [{:keys [people color-stack] :as data} pkey]
  (let [person (get people pkey)
        turning-on? (not (:displayed? person))]
    (if turning-on?
      (update-in data [:people pkey] #(-> %
                                          (assoc :displayed? true)
                                          (assoc :color (color-stack))))
      (do
        (color-stack (:color person))
        (update-in data [:people pkey] #(-> %
                                            (assoc :displayed? false)
                                            (assoc :color :black)))))))

(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (toggle-display-all)
      (assoc :all-collapsed? true)
      (toggle-collapse-all)))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

