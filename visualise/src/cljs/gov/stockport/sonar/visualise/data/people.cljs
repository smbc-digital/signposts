(ns gov.stockport.sonar.visualise.data.people
  (:require [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.util.stack :as s]
            [gov.stockport.sonar.visualise.data.merge :as merge]
            [gov.stockport.sonar.visualise.query.client :refer [keep-alive]]
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
                             (merge/merge-events (locked-events data) result))))))

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

(defn toggle-show-only-highlighted [{:keys [show-only-highlighted? show-only-highlighted-disabled?] :as data}]
  (assoc
    data
    :show-only-highlighted?
    (if show-only-highlighted-disabled? show-only-highlighted? (not show-only-highlighted?))))

(defn all-or-none-highlighted? [{:keys [people]}]
  (= 1 (count (distinct (map (fn [[_ {:keys [highlighted?]}]] (or highlighted? false)) people)))))

(defn enable-or-disable-show-only-highlighted-disabled [data]
  (assoc data :show-only-highlighted-disabled? (all-or-none-highlighted? data)))

(defn enable-or-disable-show-only-highlighted [{:keys [show-only-highlighted-disabled?] :as data}]
  (if show-only-highlighted-disabled?
    (assoc data :show-only-highlighted? false)
    data))

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
        (assoc :highlighting-allowed? (not (is-empty?)))
        (enable-or-disable-show-only-highlighted-disabled)
        (enable-or-disable-show-only-highlighted))))

(defn- sufficient-colors-for-people? [{:keys [people]}]
  (<= (count people) (count c/colour-priority)))

(defn with-initial-color [{:keys [people] :as data}]
  (if (sufficient-colors-for-people? data)
    (reduce toggle-highlight-person data (keys people))
    data))

(defn from-data [data]
  (-> data
      (by-people)
      (with-max-score)
      (with-rank)
      (assoc :all-collapsed? false
             :show-only-highlighted? false
             :show-only-highlighted-disabled? true
             :highlighting-allowed? true)
      (assoc :color-stack (s/new-stack c/colour-priority :value-when-empty :black))
      (with-initial-color)))

(defn by-rank [{:keys [people]}]
  (sort-by (fn [[_ {:keys [rank]}]] rank) people))

(defn results-summary [data]
  (defn num-people-summary [data]
    (let [num-people (count (:people data))]
      (str num-people (if (> num-people 1) " people" " person"))))
  (defn num-events-summary [data]
    (str (:total data) " event" (if (> (:total data) 1) "s")))
  (str "Your search returned " (num-events-summary data) " from " (num-people-summary data)))

(defn- clear-selected-event? [people]
  (reduce merge {}
          (map (fn [[k v]] {k (dissoc v :has-selected-event?)}) people)))

(defn select-event [{:keys [people] :as data} event]
  (let [pkey-for-event (select-keys event group-keys)]
    (-> data
        (assoc :selected-event event)
        (assoc :people (clear-selected-event? people))
        (assoc-in [:people pkey-for-event :has-selected-event?] true))))

(defn deselect-event [{:keys [people] :as data}]
  (-> data
      (dissoc :selected-event)
      (assoc :people (clear-selected-event? people))))

