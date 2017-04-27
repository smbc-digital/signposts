(ns gov.stockport.sonar.ingest.faking.events.eis
  (:require [gov.stockport.sonar.ingest.faking.helpers :as h]
            [clj-time.core :as t]
            [gov.stockport.sonar.ingest.faking.config :as cfg]
            [gov.stockport.sonar.ingest.faking.people :as people]))

(defn child-in-need-events [children household]
  (let [durations (take (+ 1 (rand-int 5)) (h/durations 24 32))
        event-type (rand-nth [:CIN :LIC])
        keyworker (rand-nth people/key-worker-pool)]
    (flatten
      (map
        (fn [{:keys [timestamp] :as duration}]
          (map
            (fn [{:keys [name dob]}]
              (merge duration {:event-source :EIS
                               :event-type   event-type
                               :name         (:full-name name)
                               :dob          dob
                               :address      (h/address-at timestamp household)
                               :keyworker    keyworker})
              ) children)
          ) durations))))

(defn eis-events [{:keys [dependents] :as household}]
  (if dependents
    (h/perhaps cfg/eis-per-household
               #(child-in-need-events dependents household))))

