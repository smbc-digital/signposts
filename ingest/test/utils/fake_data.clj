(ns fake-data
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str])
  (:use faker.name
        faker.address
        faker.phone-number))

(defn event-type []
  (rand-nth [:asbo :eviction :exclusion :arrears :accident :intervention]))

(defn dob []
  (t/date-midnight (+ 2000 (rand-int 2)) (+ 1 (rand-int 11)) (+ 1 (rand-int 27))))

(defn address []
  (str/join "," [(street-address) (uk-county) (uk-postcode)]))

(defn time-in-last-2-years []
  (t/minus (t/now) (t/days (rand-int (* 2 365)))))

(defn person []
  {:name    (first (names))
   :dob     (f/unparse (:date f/formatters) (dob))
   :address (address)})

(defn event [person]
  (merge
    person
    {:timestamp  (f/unparse (:date-time f/formatters) (time-in-last-2-years))
     :event-type (event-type)}))

(defn timeline []
  (let [someone (person)]
    (take (rand-int 15) (repeatedly #(event someone)))))

(defn timelines []
  (repeatedly timeline))