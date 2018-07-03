(ns gov.stockport.sonar.visualise.ui.event-templates.Yos
  "Youth Offending Services Templates"
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- left-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Childview Id"]]
    [:div.col.col-md-8
     (:childview-id
       event)]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Name"]]
    [:div.col.col-md-8
     (:name event)]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "DOB"]]
    [:div.col.col-md-8
     (fh/to-dob(:dob event))]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Ethnicity"]]
    [:div.col.col-md-8
     (:ethnicity event)]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Gender"]]
    [:div.col.col-md-8
     (:gender event)]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Open Date"]]
    [:div.col.col-md-8
     (:timestamp (fh/unparse-timestamp event))]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Close Date"]]
    [:div.col.col-md-8
     (fh/close-date(:close-date event))]]])

(defn- middle-column [event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-3
     [:strong "Address"]]
    [:div.col.col-md-4
     (:address-type event) [:br]
     (:address event) [:br]
     (:postcode event)]]])


(defn non-statutory-intervention[event]
  [:div.event-details
   [:div.panel-heading
  [:h4 "YOS " [:span.not-bold"Non Statutory Intervention"]]]
  [:div.row {:class "Yos"}
   (left-column event)
   (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Supervisor"]]
     [:div.col.col-md-8
      (:supervisor-name event)[:br]
      [:strong "id:"](:supervisor-id event)[:br]
      [:a {:href (str "mailto:"(:superviser-email event))} (:superviser-email event)][:br]
      (:supervisor-mobile event)[:br]
      (:supervisor-phone event)]]]]])

(defn statutory-intervention[event]
  [:div.event-details.
   [:div.panel-heading
   [:h4 "YOS " [:span.not-bold "Statutory Intervention"]]]
  [:div.row {:class "Yos"}
   (left-column event)
   (middle-column event)
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong "Supervisor"]]
    [:div.col.col-md-8
     (:supervisor-name event)[:br]
     [:strong "id:"](:supervisor-id event)[:br]
     [:a {:href (str "mailto:"(:superviser-email event))} (:superviser-email event)][:br]
     (:supervisor-mobile event)[:br]
     (:supervisor-phone event)]]]]])