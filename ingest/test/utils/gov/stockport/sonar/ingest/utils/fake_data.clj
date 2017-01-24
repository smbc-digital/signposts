(ns gov.stockport.sonar.ingest.utils.fake-data
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [gov.stockport.sonar.spec.event-spec :as es])
  (:use faker.name
        faker.address
        faker.phone-number))

(def event-sources
  [{::es/event-source :SCHOOLS
    ::es/event-types  [:EXCLUSION]}
   {::es/event-source :HOMES
    ::es/event-types  [:ARREARS :EVICTION]}
   {::es/event-source :GMP
    ::es/event-types  [:ASBO :CAUTION]}
   {::es/event-source :EIS
    ::es/event-types  [:LIC :CIN]}
   ])

(defn rand-event-source []
  (let [{:keys [event-source event-types]} (rand-nth event-sources)]
    {::es/event-source event-source
     ::es/event-type   (rand-nth event-types)}))

(defn dob []
  (t/date-midnight (+ 1995 (rand-int 10)) (+ 1 (rand-int 11)) (+ 1 (rand-int 27))))

(defn address []
  (str/join "," [(street-address) (uk-county) (uk-postcode)]))

(defn time-in-last-2-years []
  (t/minus (t/now) (t/days (rand-int (* 2 365)))))

(def people (take 100 (names)))

(defn person []
  {:name    (rand-nth people)
   :dob     (f/unparse (:date f/formatters) (dob))
   :address (address)})

(defn event [person]
  (let [{:keys [event-source event-type]} (rand-event-source)]
    (merge
      person
      {:timestamp        (f/unparse (:date-time f/formatters) (time-in-last-2-years))
       ::es/event-source event-source
       ::es/event-type   event-type})))

(defn timeline []
  (let [someone (person)]
    (take (rand-int 15) (repeatedly #(event someone)))))

(defn timelines
  ([] (timelines []))
  ([events] (lazy-seq (concat (timeline) (timelines events)))))
