(ns ingest.faking.events.simple
  (:require [ingest.faking.helpers :as h]))

(def event-sources
  [{:event-source :HOMES
    :event-types  [:ARREARS :EVICTION]}
   {:event-source :GMP
    :event-types  [:ASBO :CAUTION]}])

(defn rand-event-source []
  (let [{:keys [event-source event-types]} (rand-nth event-sources)]
    {:event-source event-source
     :event-type   (rand-nth event-types)}))

(defn event [person {:keys [dependents addresses]}]
  (let [{:keys [event-source event-type]} (rand-event-source)
        dependents (map #(select-keys % [:name :dob]) dependents)
        address (last addresses)]
    (merge
      person
      {:timestamp    (h/time-in-last-3-years)
       :event-source event-source
       :event-type   event-type
       :meta         {:last-address address
                      :dependents   dependents}})))

(defn events-for-someone [{:keys [adults] :as household}]
  (let [someone (rand-nth adults)]
    (take (rand-int 15) (repeatedly #(event someone household)))))

(defn simple-events [household]
  (events-for-someone household))