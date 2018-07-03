(ns gov.stockport.sonar.visualise.ui.contact-templates.RevsBens
  "Revenues and Benefits Templates"
  (:require
    [gov.stockport.sonar.visualise.util.fmt-help :as fh ]
    [clojure.string :as s]))

  (defn- left-column [event]
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-3
       [:strong.label "Council Tax ID"]]
      [:div.col.col-md-9
       (:eis-number event)]]
     [:div.row
      [:div.col.col-md-3
       [:strong.label "National Insurance"]]
      [:div.col.col-md-9
       (:ni-number event)]]
     [:div.row
      [:div.col.col-md-4
       [:strong.label "DOB"]]
      [:div.col.col-md-8
       (fh/to-dob(:dob event))]]
     [:div.row
      [:div.col.col-md-3
       [:strong.label "Open Date"]]
      [:div.col.col-md-9
       (:timestamp (fh/unparse-timestamp event))]]
     [:div.row
      [:div.col.col-md-3
       [:strong.label "Dependents"]]
      [:div.col.col-md-9
       (:non-dependents event)]]
     [:div.row
      [:div.col.col-md-3
       [:strong.label "Non Dependents"]]
      [:div.col.col-md-9
       (:number-of-non-dependents event)]]])

(defn- middle-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-3
     [:strong "Address"]]
    [:div.col.col-md-9
     (:address event) [:br]
     (:postcode event)]]])

(defn ct-support[event]
    [:div
     [:h4   "RevsBens " [:span.not-bold "Council Tax Support"]]
     [:div.row {:class "Yos"}
      (left-column event)
      (middle-column event)
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-3
         [:strong "Benefit Type"]]
        [:div.col.col-md-9
         (:benefit-type event)]]
       [:div.row
        [:div.col.col-md-3
         [:strong "CTS Claim status"]]
        [:div.col.col-md-9
         (:ctb-claim-status event)]]
       [:div.row
        [:div.col.col-md-3
         [:strong "Tenancy Type"]]
        [:div.col.col-md-9
         (:tenancy-type event)]]]]])

(defn ctax-bill[event]
  [:div
   [:h4   "RevsBens " [:span.not-bold "Council Tax Bill"]]
   [:div.row {:class "ctax-bill"}
   [:div.col..col-md-4
    [:div.row
     [:div.col.col-md-3
      [:strong.label "Other Name on Bill"]]
     [:div.col.col-md-9
      (:other-name-on-bill event)]]
    [:div.row
     [:div.col.col-md-3
      [:strong.label "Event Logged"]]
     [:div.col.col-md-9
      (:timestamp (fh/unparse-timestamp event))]]]
    (middle-column event)
    [:div.col.col-md-4]]])


(defn hb-cts[event]
  [:div
   [:h4   "RevsBens " [:span.not-bold "Housing Benefit and Council Tax Support"]]
   [:div.row {:class "Yos"}
    (left-column event)
    (middle-column event)
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-9
       [:strong "Benefit Type"]]
      [:div.col.col-md-9
       (:benefit-type event)]]
     [:div.row
      [:div.col.col-md-9
       [:strong "HB Claim status"]]
      [:div.col.col-md-9
       (:hb-claim-status event)]]
     [:div.row
      [:div.col.col-md-3
       [:strong "CTS Claim status"]]
      [:div.col.col-md-9
       (:ctb-claim-status event)]]
     [:div.row
      [:div.col.col-md-9
       [:strong "Claim status"]]
      [:div.col.col-md-9
       (:hb-claim-status event)]]
     [:div.row
      [:div.col.col-md-9
       [:strong "Tenancy Type"]]
      [:div.col.col-md-9
       (:tenancy-type event)]]]]])

