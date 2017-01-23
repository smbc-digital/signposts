(ns gov.stockport.sonar.ingest.faking.households
  (:require [gov.stockport.sonar.ingest.faking.schools :as schools]
            [gov.stockport.sonar.ingest.faking.people :refer [adult child likely-related likely-unrelated]]
            [gov.stockport.sonar.ingest.faking.helpers :as h]
            [gov.stockport.sonar.ingest.faking.addresses :as addresses]
            [gov.stockport.sonar.ingest.faking.config :as cfg]
            [gov.stockport.sonar.ingest.faking.addresses :as address]
            [clj-time.core :as t]))


(defn in-district [household]
  (assoc household :district (rand-nth addresses/districts)))

(defn with-children [household]
  (let [family-name (:name (first (:adults household)))]
    (assoc household
      :dependents
      (h/up-to cfg/max-dependent-children #(child family-name)))))

(defn with-addresses [{:keys [district] :as household}]
  (let [address-dates (take (+ 1 (rand-int cfg/max-addresses-per-household)) (h/address-dates))
        addresses (map (fn [from]
                         {:from    from
                          :address (address/address-in-district district)}) address-dates)]
    (assoc household :addresses addresses)))

(defn single-parent-household []
  (->
    {:adults [(adult)]}
    in-district
    with-addresses
    with-children
    schools/with-schooling))

(defn two-parent-household []
  (let [parent-one (adult)
        parent-two (adult (:name parent-one) likely-related)]
    (->
      {:adults [parent-one parent-two]}
      in-district
      with-addresses
      with-children
      schools/with-schooling)))

(defn lone-adult-household []
  (->
    {:adults [(adult)]}
    in-district
    with-addresses))

(defn two-adult-household []
  (let [adult-one (adult)
        adult-two (adult (:name adult-one) likely-unrelated)]
    (->
      {:adults [adult-one adult-two]}
      in-district
      with-addresses)))

