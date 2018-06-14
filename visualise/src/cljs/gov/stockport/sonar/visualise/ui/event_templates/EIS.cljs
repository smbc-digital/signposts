(ns gov.stockport.sonar.visualise.ui.event-templates.EIS
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [clojure.string :as s]))

(defn- primary-presenting-issue[event]
  (let [issue(:primary-presenting-issue event)]
      (s/replace issue #"[{}]" "")
    ))

(defn- left-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "EIS Number"]]
    [:div.col.col-md-8
     (:eis-number event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Unique Pupil ID"]]
    [:div.col.col-md-8
     (:unique-pupil-number event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Name"]]
    [:div.col.col-md-8
     (:name event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "DOB"]]
    [:div.col.col-md-8
     (fh/to-dob(:dob event))
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Open Date"]]
    [:div.col.col-md-8
     (:timestamp (fh/unparse-timestamp event))
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Close Date"]]
    [:div.col.col-md-8
     (fh/eis-close-date(:end-date event))]]])

(defn- middle-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-sm-3
     [:strong "Address"]]
    [:div.col.col-sm-9
     (:address event)[:br]
     (:postcode event)]]])

(defn contact [event]
  [:div.event-details
   [:div.panel-heading
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Contact"]]]
  [:div.row {:class "eis-in"}
   (left-column event)
   (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Presenting Issue"]]
     [:div.col.col-md-8
      (primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Outcome"]]
     [:div.col.col-md-8
      "Proceed to Social Care Referral"]]]]])

(defn cin [event]
  [:div.event-details
   [:div.panel-heading
  [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Child in Need"]]]
  [:div.row {:class "cin"}
   (left-column event)
   (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Presenting Issue"]]
     [:div.col.col-md-8
      (primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Closure reason"]]
     [:div.col.col-md-8
      (:closure event)]]]]])

(defn lac [event]
  [:div.event-details
   [:div.panel-heading
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Looked-after child"]]]
   [:div.row {:class "cin"}
    (left-column event)
    (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Presenting Issue"]]
     [:div.col.col-md-8
      (:primary-presenting-issue event)]]]]])

(defn sen [event]
  [:div.event-details
   [:div.panel-heading
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Special Educational Needs"]]]
   [:div.row {:class "cin"}
    (left-column event)
    (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Presenting Issue"]]
     [:div.col.col-md-8
      (:primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "On going"]]
     [:div.col.col-md-8
      (:on-going event)]]]]])