(ns gov.stockport.sonar.visualise.ui.search.search-controls
  (:require [reagent.core :as r]))

(defn name[]
  [:label "Name"
   [:input
    {:type "text"
     :pattern "^[a-zA-Z]{1,2}"}
     :name "name"
     :placeholder "what postcode?"]])

(defn address[]
  [:label "Address"
   [:input
    {:type type
     :pattern "^[a-zA-Z]{1,2}"
     :name "address"
     :id "address"
     :placeholder "what address?"}]])

(defn postcode[]
  [:label "Postcode"
   [:input
    {:type "text"
     :pattern "^[a-zA-Z]{1,2}[0-9]{1,2}"
     :name "postcode"
     :id "postcode"
     :placeholder "what postcode"}]])

(defn dob[]
  [:label "Date of birth"
   [:input
    {:type "date"
     :name "dob"
     :id "dob"}]])

(defn age-less-than[]
  [:label "Aged up to"
   [:input
    {:type "number"
     :min "1" :max "150"
     :placeholder "maximum age in years"
     :name "age=less=than"
     :id "age=less=than" }]])

(defn age-more-than[]
  [:label "Aged Over"
   [:input
    {:type "number"
     :min "0"
     :max "100"
     :name "age-more-than"
     :id  "age-more-than"
     :placeholder "minimum age in years"}]])

(defn all-fields[]
  [:label "All fields"
   [:input
    {:type "text"
     :name "all-fields"
     :placeholder "general search"
     :id "all-fields"}]])

(defn event-source[]
  [:label "Event Source"
   [:input
    {:type "text"
     :name "event-type"
     :id "event-type"
     :placeholder "search for event source"}]])

(defn event-type[]
  [:label "Event Type"
   [:input
    {:type "text"
     :name "event-type"
     :id "event-type"
     :placeholder "search for an event type"}]])
