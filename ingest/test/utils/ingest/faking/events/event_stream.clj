(ns ingest.faking.events.event-stream
  (:require [ingest.faking.households :as hh]
            [ingest.faking.helpers :as h]
            [ingest.faking.events.schools :refer [school-events]]
            [ingest.faking.events.eis :refer [eis-events]]
            [ingest.faking.events.simple :refer [simple-events]]
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
(defn is-joda? [v] (str/starts-with? (.getName ^Class (type v)) "org.joda"))

(defn fmt [date] (f/unparse standard-date-time-format date))

(defn cleanup-dates [m]
  (let [f (fn [[k v]] (if (is-joda? v) [k (fmt v)] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn timeline []
  (let [household (next-household)]
    (map cleanup-dates
         (flatten
           (map
             (fn [events-fn] (events-fn household))
             [school-events
              eis-events
              simple-events])))))

(defn timelines
  ([] (timelines []))
  ([events] (lazy-seq (concat (timeline) (timelines events)))))