(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.util.stack :as s]
            [gov.stockport.sonar.visualise.data.merge :as merge]
            [clojure.string :as str]))

(def group-keys [:name :dob :address :postcode])

(defn locked-pkeys [{:keys [people]}]
  (into #{} (filter not-empty (map (fn [[pkey {:keys [locked?]}]] (when locked? pkey)) people))))

(defn- all-event-data [[_ {:keys [data]}]]
  data)

(defn- locked-event-data [[_ {:keys [locked? data]}]]
  (when locked? data))

(defn- extractor-for [extractor]
  (fn [{:keys [people]}]
    (reduce concat [] (map extractor people))))

(def all-events (extractor-for all-event-data))

(def locked-events (extractor-for locked-event-data))

(defn by-people [{:keys [result] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k {:data v}})
                           (group-by
                             (fn [m] (select-keys m group-keys))
                             ;result
                             (merge/merge-events (locked-events data) result)
                             )))))

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

(defn toggle-show-only-highlighted [{:keys [show-only-highlighted?] :as data}]
  (assoc data :show-only-highlighted? (not show-only-highlighted?)))

(defn toggle-highlight-person [{:keys [people color-stack] :as data} pkey]
  (let [{:keys [pop push is-empty?]} color-stack
        person (get people pkey)
        turning-on? (not (:highlighted? person))]
    (-> (if turning-on?
          (update-in data [:people pkey] #(-> %
                                              (assoc :highlighted? true)
                                              (assoc :color (pop))))
          (do
            (push (:color person))
            (update-in data [:people pkey] #(-> %
                                                (assoc :highlighted? false)
                                                (assoc :color :black)))))
        (assoc :highlighting-allowed? (not (is-empty?))))))


(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (assoc :all-collapsed? true :show-only-highlighted? true :highlighting-allowed? true)
      (toggle-show-only-highlighted)
      (toggle-collapse-all)
      (assoc :color-stack (s/new-stack c/colour-priority :value-when-empty :black))))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

(defn results-summary [data]
  (defn num-people-summary [data]
    (let [num-people (count (:people data))]
      (str num-people (if (> num-people 1) " people" " person"))))
  (defn num-events-summary [data]
    (str (:total data) " event" (if (> (:total data) 1) "s")))
  (str "Your search returned " (num-events-summary data) " from " (num-people-summary data)))
