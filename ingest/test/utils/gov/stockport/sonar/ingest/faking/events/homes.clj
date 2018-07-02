(ns gov.stockport.sonar.ingest.faking.events.homes
  (:require [gov.stockport.sonar.ingest.faking.helpers :as h]
            [clj-time.core :as t]
            [gov.stockport.sonar.ingest.faking.config :as cfg]
            [gov.stockport.sonar.ingest.faking.people :as people]
            [gov.stockport.sonar.ingest.faking.helpers :refer [make]]))

(defn home-events [{:keys [adults] :as household}]
  (let [durations (take (+ 1 (rand-int 5)) (h/durations 24 32))
        event-type (rand-nth [:ASB :6-WEEK-ARREARS :NOTICE-POSSESSION :EVICTIONS ])
        keyworker (rand-nth people/key-worker-pool)]
    (map
      (fn [{:keys [timestamp] :as duration}]
        (let [{:keys [name nino dob]} (rand-nth adults)]
          (merge duration {:event-source :STOCKPORT-HOMES
                           :event-type   event-type
                           :name         (:full-name name)
                           :dob          dob
                           :nino         nino
                           :other-information "Other Information Placeholder"
                           :address      (h/address-at timestamp household)
                           :keyworker    (-> keyworker :name :full-name)})))
      durations)))

(defn homes-events [household]
  (h/perhaps cfg/homes-per-household
             #(home-events household)))

