(ns gov.stockport.sonar.visualise.ui.search.search-controls
  (:require [reagent.core :as r]))

(defn full-name[]
  [:label "Name"
    [:input.form-control
     {
     :type    "text"
     :pattern "^[a-zA-Z]{1,2}"
     :name "name"
     :placeholder "what name?"}]])

(defn address[]
  [:label "Address"
   [:input.form-control
    {:type :text
     :pattern "^[a-zA-Z]{1,2}"
     :name "address"
     :id "address"
     :placeholder "what address?"}]])

(defn postcode[]
  [:label "Postcode"
   [:input.form-control
    {:type "text"
     :pattern "^[a-zA-Z]{1,2}[0-9]{1,2}"
     :name "postcode"
     :id "postcode"
     :placeholder "what postcode"}]])

(defn dob[]
  [:label "Date of birth"
   [:input.form-control
    {:type "date"
     :name "dob"
     :id "dob"}]])

(defn age-less-than[]
  [:label "Aged up to"
   [:input.form-control
    {:type "number"
     :min "1" :max "150"
     :placeholder "maximum age in years"
     :name "age=less=than"
     :id "age=less=than" }]])

(defn age-more-than[]
  [:label "Aged Over"
   [:input.form-control
    {:type "number"
     :min "0"
     :max "100"
     :name "age-more-than"
     :id  "age-more-than"
     :placeholder "minimum age in years"}]])

(defn age-range[]
  [:div
  [:label "Aged Over"
   [:input.form-control
    {:type "number"
     :min "0"
     :max "100"
     :name "age-more-than"
     :id  "age-more-than"
     :placeholder "minimum age in years"}]
   [:label "Aged up to"
    [:input.form-control
     {:type "number"
      :min "1" :max "150"
      :placeholder "maximum age in years"
      :name "age=less=than"
      :id "age=less=than" }]]]])



(defn all-fields[]
  [:label "All fields"
   [:input.form-control
    {:type "text"
     :name "all-fields"
     :placeholder "general search"
     :id "all-fields"}]])

(defn event-source[]
  [:label "Event Source"
   [:input.form-control
    {:type "text"
     :name "event-type"
     :id "event-type"
     :placeholder "search for event source"}]])

(defn event-type[]
  [:label "Event Type"
   [:input.form-control
    {:type "text"
     :name "event-type"
     :id "event-type"
     :placeholder "search for an event type"}]])




(def search-control-map
  {
    :full-name full-name
    :address address
    :postcode postcode
    :dob dob
    :age-less-than age-less-than
    :age-more-than age-more-than
    :all-fields all-fields
    :event-source event-source
    :event-type event-type})

(defn get-seach-control[search-term]
  ((keyword search-term) search-control-map full-name))