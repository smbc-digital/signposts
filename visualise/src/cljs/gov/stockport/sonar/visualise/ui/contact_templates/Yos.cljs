(ns gov.stockport.sonar.visualise.ui.contact-templates.Yos
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- left-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Childview Id"]]
    [:div.col.col-md-8
     (:child-view-id event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Ethnicity"]]
    [:div.col.col-md-8
     (:ethnicity event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Gender"]]
    [:div.col.col-md-8
     (:gender event)
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
     (:end-date event)
     ]]
   ]
  )

(defn- middle-column [event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-3
     [:strong "Address"]
     ]
    [:div.col.col-md-8
     (:address-type event) [:br]
     (:address event) [:br]
     (:postcode event)]
    ]]
  )


(defn non-statutory-intervention[event]
  [:div
  [:h4   "YOS " [:span {:style {:font-weight "normal"}} "Non Statutory Intervention"]]
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
      (:supervisor-phone event)
      ]]]]])

(defn statutory-intervention[event]
  [:div
  [:h4   "YOS " [:span {:style {:font-weight "normal"}} "Statutory Intervention"]]
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
     (:supervisor-phone event)
     ]]]]])