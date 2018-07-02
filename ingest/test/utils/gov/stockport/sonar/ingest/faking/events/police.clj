(ns gov.stockport.sonar.ingest.faking.events.police
    (:require [gov.stockport.sonar.ingest.faking.helpers :as h]
      [clj-time.core :as t]
      [gov.stockport.sonar.ingest.faking.config :as cfg]
      [gov.stockport.sonar.ingest.faking.people :as people]
      [gov.stockport.sonar.ingest.faking.helpers :refer [make]]))

(defn- crime-outcome[]
      (rand-nth ["Caution" "No Charge" "Charge"]))

(defn victim-urn[]
      (+ 1000000 (rand-int 999999)))

(defn source-crime-ref[]
  (+ 1000000 (rand-int 999999)))


(defn police-event [{:keys [adults] :as household}]
      (let [durations (take (+ 1 (rand-int 5)) (h/durations 24 32))
            event-type :DOMESTIC-VIOLENCE
            keyworker (rand-nth people/key-worker-pool)]
           (map
             (fn [{:keys [timestamp] :as duration}]
                 (let [{:keys [name nino dob]} (rand-nth adults)]
                      (merge duration {:event-source :GMP
                                       :event-type   event-type
                                       :victime-urn  (victim-urn)
                                       :source-crime-ref (source-crime-ref)
                                       :name         (:full-name name)
                                       :dob          dob
                                       :nino         nino
                                       :victim-gender "female"
                                       :HO-crime-category "Violence againts the person"
                                       :HO-offence-group "Assault without Injury"
                                       :crime-outcome-short-description (crime-outcome)
                                       :address      (h/address-at timestamp household)
                                       :keyworker    (-> keyworker :name :full-name)})))
             durations)))

(defn police-events [household]
      (h/perhaps cfg/homes-per-household
                 #(police-event household)))
