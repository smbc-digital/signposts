(ns gov.stockport.sonar.visualise.ui.contact-templates.Yos
  "Event Template Youth Offending Services"
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- format-phone-number [number]
   (if (clojure.string/blank? number)
     ""
     (str "0" number)))


  (defn- left-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Childview Id"]]
    [:div.col.col-md-8
     (::childview-id event)]]
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
     [:strong.label "DOB"]]
    [:div.col.col-md-8
     (fh/to-dob(:dob event))]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Open Date"]]
    [:div.col.col-md-8
     (:timestamp (fh/unparse-timestamp event))]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Close Date"]]
    [:div.col.col-md-8
     (fh/close-date(:end-date event))]]])

(defn- middle-column [event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-3
     [:strong "Address"]]
    [:div.col.col-md-8
     (:address-type event) [:br]
     (:address event) [:br]
     (:postcode event)]]])

(defn non-statutory-intervention[event]
  [:div
  [:h4   "YOS " [:span.not-bold "Non Statutory Intervention"]]
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
      [:a {:href (str "mailto:"(:supervisor-email event))} (:supervisor-email event)][:br]
      (format-phone-number(:supervisor-mobile event))[:br]
      (format-phone-number(:supervisor-phone event))]]]]])

(defn statutory-intervention[event]
  [:div
  [:h4   "YOS " [:span.not-bold "Statutory Intervention"]]
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
     [:a {:href (str "mailto:"(:supervisor-email event))} (:supervisor-email event)][:br]
     (format-phone-number(:supervisor-mobile event))[:br]
     (format-phone-number(:supervisor-phone event))]]]]])