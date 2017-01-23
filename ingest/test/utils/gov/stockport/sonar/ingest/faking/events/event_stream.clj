(ns gov.stockport.sonar.ingest.faking.events.event-stream
  (:require [gov.stockport.sonar.ingest.faking.households :as hh]
            [gov.stockport.sonar.ingest.faking.helpers :as h]
            [gov.stockport.sonar.ingest.faking.events.schools :refer [school-events]]
            [gov.stockport.sonar.ingest.faking.events.eis :refer [eis-events]]
            [gov.stockport.sonar.ingest.faking.events.simple :refer [simple-events]]
            [clojure.walk :as walk]
            [clj-time.format :as f]
            [clojure.string :as str]))

(def weighted-household-types
  (h/weighted-random-fn
    [[2 hh/lone-adult-household]
     [2 hh/two-adult-household]
     [1 hh/single-parent-household]
     [5 hh/two-parent-household]]))

(defn next-household []
  (let [next-fn (weighted-household-types)]
    (next-fn)))

(def standard-date-time-format (:date-time f/formatters))
(def standard-date-format (:date f/formatters))
(defn is-joda? [v] (str/starts-with? (.getName ^Class (type v)) "org.joda"))

(defn fmt-dt [date] (f/unparse standard-date-time-format date))
(defn fmt-d [date] (f/unparse standard-date-format date))

(defn cleanup-dates [m]
  (let [f (fn [[k v]]
            (if (= :dob k)
              [k (fmt-d v)]
              (if (is-joda? v) [k (fmt-dt v)] [k v])))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(def not-nil? (complement nil?))

(defn timeline []
  (let [household (next-household)]
    (map cleanup-dates
         (filter not-nil?
                 (flatten
                   (map
                     (fn [events-fn] (events-fn household))
                     [school-events
                      eis-events
                      simple-events]))))))

(defn timelines
  ([] (timelines []))
  ([events] (lazy-seq (concat (timeline) (timelines events)))))