(ns gov.stockport.sonar.visualise.ui.contact-templates.GMP
  "Greate Manchester Police Templates"
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]
  [clojure.string :as str]))

(defn asbo [event]
  [:div
  [:h4   "Police " [:span.not-bold "ASBO"]]
   [:div.row {:class "ASBO"}
     [:div.col.col-md-4
         [:div.row
         [:div.col.col-md-3
         [:strong.label "Date issued"]]
         [:div.col.col-md-9
          (:timestamp (fh/unparse-timestamp event))]]]
      [:div.col.col-md-4
        [:div.row
        [:div.col.col-md-3
        [:strong "Address"]]
        [:div.col.col-md-9
         (:address event)]]]
      [:div.col.col-md-4
       [:div.row
       [:div.col.col-md-4
       [:strong "Postcode"]]
       [:div.col.col-md-8
       (:postcode event)]]]]])

(defn caution [event]
  [:div
  [:h4   "Police " [:span.not-bold "Caution"]]
  [:div.row {:class "Caution"}
   [:div.col..col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Date issued"]]
     [:div.col.col-md-3
      (:timestamp (fh/unparse-timestamp event))]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Address"]]
     [:div.col.col-md-3
      (:address event)]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-2-sm
      [:strong "Postcode"]]
     [:div.col.col-2-sm
      (:postcode event)]]]]])

(defn domestic [event]
  [:div
  [:h4   "Police " [:span.not-bold "Domestic Violence"]]
  [:div.row {:class "gmp domestic"}
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Crime Number"]]
     [:div.col.col-md-8
      (:source-crime-ref event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Victim Unique Reference Number"]]
     [:div.col.col-md-8
      (:victim-urn event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Victim Gender"]]
     [:div.col.col-md-8
      (:gender event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Open Date"]]
     [:div.col.col-md-8
      (:timestamp (fh/unparse-timestamp event))]]
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Crime Date"]]
     [:div.col.col-md-8
      (:crime-committed-from-full-date event)]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-3
      [:strong "Address"]]
     [:div.col.col-md-9
      (:address event)
      [:br]
      (:postcode event)]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Crime Category"]]
     [:div.col.col-md-8
      (:ho-crime-category event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Offence Group"]]
     [:div.col.col-md-8
      (:ho-offence-group event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Outcome"]]
     [:div.col.col-md-8
      (:crime-outcome-short-description event)]]]]])