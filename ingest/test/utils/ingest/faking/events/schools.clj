(ns ingest.faking.events.schools
  (:require [ingest.faking.helpers :as h]
            [ingest.faking.schools :refer [schools]]
            [clj-time.core :as t]
            [ingest.faking.config :as cfg]))


(def school-months [9 10 11 12 1 2 3 4 5 6])

(defn exclusion-dates [{:keys [from]}]
  (let [from-year (t/year from)
        months (take (+ 1 (rand-int cfg/max-exclusions)) (shuffle school-months))]
    (map #(t/date-midnight from-year % (+ 1 (rand-int 14))) months)))

(defn exclude-from [school {:keys [name dob]}]
  (let [timestamps (exclusion-dates school)]
    (map (fn [timestamp]
           {:event-source :SCHOOLS
            :event-type   :EXCLUSION
            :timestamp    timestamp
            :duration      (rand-int 14)
            :name         name
            :dob          dob
            :meta         {:school (get @schools (:school school))}
            }) timestamps)))

(defn randomly-exclude [dependents]
  (let [children (take (+ 1 (rand-int 2)) (shuffle dependents))]
    (map #(exclude-from (rand-nth (rest (:schooling %))) %) children)))

(defn dependents-at-junior-school-or-above [dependents]
  (filter #(> (count (:schooling %)) 1) dependents))

(defn exclusions [{:keys [dependents]}]
  (if dependents
    (h/perhaps cfg/exclusions-per-household
               #(randomly-exclude
                 (dependents-at-junior-school-or-above dependents)))))