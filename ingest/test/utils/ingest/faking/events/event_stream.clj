(ns ingest.faking.events.event-stream
  (:require [ingest.faking.households :as hh]
            [ingest.faking.helpers :as h]
            [ingest.faking.events.schools :refer [exclusions]]
            [ingest.faking.events.eis :refer [child-in-need]]))

(def weighted-household-types
  (h/weighted-random-fn
    [
     ;[2 hh/lone-adult-household]
     ;[2 hh/two-adult-household]
     ;[1 hh/single-parent-household]
     [5 hh/two-parent-household]]))

(defn next-household []
  (let [next-fn (weighted-household-types)]
    (next-fn)))

(defn timeline []
  (let [household (next-household)]
    (flatten (map (fn [events-fn] (events-fn household)) [exclusions child-in-need]))))

(defn timelines
  ([] (timelines []))
  ([events] (lazy-seq (concat (timeline) (timelines events)))))


