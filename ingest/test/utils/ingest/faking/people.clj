(ns ingest.faking.people
  (:require [clj-time.core :as t]
            [faker.name :as name]
            [ingest.faking.helpers :as h]
            [ingest.faking.config :as cfg]
            [ingest.faking.phone :as phone])
  (:import (java.util UUID)))


(defn- year-of-birth [[lower upper]]
  (- (t/year (t/now)) (+ 1 lower (rand-int (- upper lower)))))

(defn- dob [range]
  (fn []
    (t/date-midnight (year-of-birth range) (+ 1 (rand-int 11)) (+ 1 (rand-int 27)))))

(def child-dob (dob cfg/child-age-range))

(def adult-dob (dob cfg/adult-age-range))

(defn persons-name []
  (let [fn (name/first-name)
        ln (name/last-name)]
    {:full-name (str fn " " ln)
     :surname   ln}))

(def name-pool (take cfg/unique-names (repeatedly persons-name)))

(defn relative [{:keys [surname]}]
  {:full-name (str (name/first-name) " " surname)
   :surname   surname})

(defn stranger [_]
  (rand-nth name-pool))

(def likely-related (h/weighted-random-fn [[5 relative] [1 stranger]]))

(def likely-unrelated (h/weighted-random-fn [[1 relative] [5 stranger]]))

(defn child [family-name]
  (let [name-fn (likely-related)]
    (->
      {:name (name-fn family-name)
       :dob  (child-dob)
       :uid  (UUID/randomUUID)}
      )))

(defn adult
  ([]
   (adult (rand-nth name-pool) likely-unrelated))
  ([family-name weight-fn]
   (let [name-fn (weight-fn)]
     {:name (name-fn family-name)
      :dob  (adult-dob)
      :uid  (UUID/randomUUID)})))

(defn employee []
  {:name  (rand-nth name-pool)
   :phone (phone/phone-number)})

(def key-worker-pool (take 50 (repeatedly #(employee))))