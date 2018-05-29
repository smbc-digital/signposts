(ns gov.stockport.sonar.ingest.faking.events.schools
  (:require [gov.stockport.sonar.ingest.faking.helpers :as h]
            [gov.stockport.sonar.ingest.faking.schools :refer [schools]]
            [clj-time.core :as t]
            [gov.stockport.sonar.ingest.faking.config :as cfg]))


(def school-months [9 10 11 12 1 2 3 4 5 6])

(defn event-dates [{:keys [from]}]
  (let [from-year (t/year from)
        months (take (+ 1 (rand-int cfg/max-exclusions)) (shuffle school-months))]
    (map #(t/date-midnight from-year % (+ 1 (rand-int 14))) months)))

(defn school-event [event-type [school {:keys [name dob]}]]
  (let [timestamps (event-dates school)]
    (map (fn [timestamp]
           {:event-source :SCHOOLS
            :event-type   event-type
            :timestamp    timestamp
            :duration     (+ 1 (rand-int 14))
            :name         (:full-name name)
            :dob          dob
            :meta         {:school (get @schools (:school school))}
            }) timestamps)))

(defn randomly-select-from [dependents]
  (let [children (take (+ 1 (rand-int cfg/max-dependent-children)) (shuffle dependents))]
    (map (fn [child] [(rand-nth (rest (:schooling child))) child]) children)))

(defn dependents-at-junior-school-or-above [dependents]
  (filter #(> (count (:schooling %)) 1) dependents))

(defn exclusions [{:keys [dependents]}]
  (if dependents
    (h/perhaps cfg/exclusions-per-household
               (fn []
                 (map (partial school-event :EXCLUSIONS)
                      (randomly-select-from
                        (dependents-at-junior-school-or-above dependents)))))))

(defn awol [{:keys [dependents]}]
  (if dependents
    (h/perhaps cfg/awol-per-household
               (fn []
                 (map (partial school-event :AWOL)
                      (randomly-select-from
                        (dependents-at-junior-school-or-above dependents)))))))

(defn school-events [household]
  (concat
    (exclusions household)
    (awol household)))