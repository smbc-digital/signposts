(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.util.stack :as s]
            [gov.stockport.sonar.visualise.data.merge :as merge]
            [gov.stockport.sonar.visualise.query.client :refer [keep-alive]]
            [clojure.string :as str]))

(def group-keys [:name :dob])

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
                             (merge/merge-events (locked-events data) result))))))

(defn with-max-score [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :score (apply max (map :score (:data v))))}) people))))

(def area #(first (str/split (or (:postcode %) "") #" ")))

(defn with-areas [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map (fn [[k v]] {k (assoc v :areas (into #{} (remove str/blank? (map area (:data v)))))}) people))))

(def surname #(last (str/split (:name %) #" ")))

(defn with-rank [{:keys [people] :as data}]
  (assoc data :people
              (reduce merge {}
                      (map-indexed
                        (fn [idx [k v]] {k (assoc v :rank (+ 1 idx))})
                        (sort-by (fn [[_ p]] [(- 0 (:score p)) (surname p)]) people)))))

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
      (with-areas)
      (assoc :highlighting-allowed? true)
      (assoc :color-stack (s/new-stack c/colour-priority :value-when-empty :black))))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))



(defn results-summary [data]
  (defn num-people-summary [data]
    (let [num-people (count (:people data))]
      (str num-people (if (> num-people 1) " people" " person"))))
  (defn num-events-summary [data]
    (str (:total data) " event" (if (> (:total data) 1) "s")))
  (if (> (:total data) 0)
    (str "Your search returned " (num-events-summary data) " concerning " (num-people-summary data))
    "Sorry, that search returned no results"))

(defn- clear-selected-event? [people]
  (reduce merge {}
          (map (fn [[k v]] {k (dissoc v :has-selected-event?)}) people)))

(defn clear-selected-people [data]
  (from-data data))



(defn deselect-event [{:keys [people] :as data}]
  (-> data
      (dissoc :selected-event)
      (assoc :people (clear-selected-event? people))))

(defn toggle-event [{:keys [people selected-event] :as data} event]
  (if (= selected-event event)
    (deselect-event data)
    (let [pkey-for-event (select-keys event group-keys)]
      (-> data
          (assoc :selected-event event)
          (assoc :people (clear-selected-event? people))
          (assoc-in [:people pkey-for-event :has-selected-event?] true)))))