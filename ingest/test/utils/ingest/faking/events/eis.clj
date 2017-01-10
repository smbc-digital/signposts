(ns ingest.faking.events.eis
  (:require [ingest.faking.helpers :as h]
            [clj-time.core :as t]
            [ingest.faking.config :as cfg]
            [ingest.faking.people :as people]))

(defn child-in-need-events [children]
  (let [durations (take (+ 1 (rand-int 5)) (h/durations 24 32))
        event-type (rand-nth [:CHILD_IN_NEED :LOOKED_AFTER_IN_CARE])
        keyworker (rand-nth people/key-worker-pool)]
    (flatten
      (map
        (fn [duration]
          (map
            (fn [{:keys [name dob]}]
              (merge duration {:event-source :EIS
                               :event-type   event-type
                               :name         name
                               :dob          dob
                               :keyworker    keyworker})
              ) children)
          ) durations))))

(defn eis-events [{:keys [dependents]}]
  (if dependents
    (h/perhaps cfg/eis-per-household
               #(child-in-need-events dependents))))

