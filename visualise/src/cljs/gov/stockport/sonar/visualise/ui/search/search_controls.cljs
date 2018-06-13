(ns gov.stockport.sonar.visualise.ui.search.search-controls
  (:require [reagent.core :as r]))

(defn full-name[]
  [:label "What name?" {:style {:width "100%"}}
    [:input.form-control
     {:type "text"
     :size "10"
     :pattern "^[a-zA-Z]{1,2}"
     :name "search-term"
     :id "search-term"
     }]])

(defn address[]
  [:label "Address" {:style {:width "100%"}}
   [:input.form-control
    {:type :text
     :pattern "^[a-zA-Z]{1,2}"
     :name "search-term"
     :id "search-term"
     :size "15"
     :placeholder "what address?"}]])

(defn postcode[]
  [:label "Postcode" {:style {:width "100%"}}
   [:input.form-control
    {:type "text"
     :size "8"
     :pattern "^[a-zA-Z]{1,2}[0-9]{1,2}"
     :name "search-term"
     :id "search-term"}]])

(defn dob[]
  [:label "Date of birth" {:style {:width "100%"}}
   [:input.form-control
    {:type "date"
     :name "search-term"
     :size "10"
     :id "dob"}]])

(defn age-less-than[]
  [:label "Aged up to" {:style {:width "100%"}}
   [:input.form-control
    {:type "number"
     :min "1" :max "150"
     :size "3"
     :placeholder "search-term"
     :name "search-term"
     :id "search-term" }]])

(defn age-more-than[]
  [:label "Aged Over"
   [:input.form-control {:style {:width "100%"}}
    {:type "number"
     :size "3"
     :min "0"
     :max "100"
     :name "search-term"
     :id  "search-term"}]])

(defn age-range[]
  [:div
  [:label "Aged Over"
   [:input.form-control{:style {:width "100%"}}
    {:type "number"
     :min "0"
     :max "100"
     :size "3"
     :name "search-term"
     :id  "search-term"
     :placeholder "minimum age in years"}]
   [:label "Aged up to" {:style {:width "100%"}}
    [:input.form-control
     {:type "number"
      :min "1" :max "150"
      :size "3"
      :placeholder "maximum age in years"
      :name "age=less=than" :id "age=less=than" }]]]])

(defn all-fields[]
  [:label "All fields" {:style {:width "100%"}}
   [:input.form-control
    {:type "text"
     :name "search-term"
     :id "search-term"
     :size "10"}]])

(defn event-source[]
  [:label "Event Source" {:style {:width "100%"}}
   [:input.form-control
    {:type "text"
     :size "10"
     :name "search-term"
     :id"search-term"}]])

(defn event-type[]
  [:label "Event Type" {:style {:width "100%"}}
   [:input.form-control
    {:type "text"
     :name "search-term"
     :id "search-term"
     :size "10"}]])


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