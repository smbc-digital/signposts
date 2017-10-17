(ns gov.stockport.sonar.visualise.ui.event-templates.Yos
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- left-column[event]
  [:div.col..col-4-sm
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Childview Id"]]
    [:div.col-3-sm
     (:child-view-id event)
     ]]
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Ethnicity"]]
    [:div.col-3-sm
     (:ethnicity event)
     ]]
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Gender"]]
    [:div.col-3-sm
     (:gender event)
     ]]

   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Open Date"]]
    [:div.col-3-sm
     (:timestamp (fh/unparse-timestamp event))
     ]]
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Close Date"]]
    [:div.col-3-sm
     (:close-date event)
     ]]
   ]
  )

(defn- middle-column [event]
  [:div.col.col-4-sm
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address"]
     ]
    [:div.col.col-9-sm
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
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Supervisor"]]
     [:div.col.col-8-sm
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
  [:div.col.col-4-sm
   [:div.row
    [:div.col.col-4-sm
     [:strong "Supervisor"]]
    [:div.col.col-8-sm
     (:supervisor-name event)[:br]
     [:strong "id:"](:supervisor-id event)[:br]
     [:a {:href (str "mailto:"(:superviser-email event))} (:superviser-email event)][:br]
     (:supervisor-mobile event)[:br]
     (:supervisor-phone event)
     ]]]]])