(ns gov.stockport.sonar.ingest.faking.events.simple
  (:require [gov.stockport.sonar.ingest.faking.helpers :as h]))

(def event-sources
  [{:event-source :GMP
    :event-types  [:DOMESTIC]}])

(defn rand-event-source []
  (let [{:keys [event-source event-types]} (rand-nth event-sources)]
    {:event-source event-source
     :event-type   (rand-nth event-types)}))

(defn event [{:keys [name dob]} {:keys [dependents] :as household}]
  (let [{:keys [event-source event-type]} (rand-event-source)
        timestamp (h/time-in-last-3-years)
        dependents (map #(select-keys % [:name :dob]) dependents)]
    {:name         (:full-name name)
     :dob          dob
     :timestamp    (h/time-in-last-3-years)
     :event-source event-source
     :event-type   event-type
     :address      (h/address-at timestamp household)
     :meta         {:dependents dependents}}))

(defn events-for-someone [{:keys [adults] :as household}]
  (let [someone (rand-nth adults)]
    (take (rand-int 15) (repeatedly #(event someone household)))))

(defn simple-events [household]
  (events-for-someone household))