(ns gov.stockport.sonar.visualise.ui.contact-templates.StockportHomes
  (:require
    [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

  (defn- middle-column[event]
           [:div.col.col-md-4
            [:div.row
             [:div.col.col-md-3
              [:strong "Address"]]
             [:div.col.col-md-9
              (:address event)]]])

  (defn- right-column[event]
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-3
       [:strong "Key worker"]]
      [:div.col.col-md-9
       (:keyworker event)]]
     (when(some? (:otherinfo event ))
     [:div.row
      [:div.col.col-md-3
       [:strong "Other Information"]]
      [:div.col.col-md-9
       (:otherinfo event)]])])

  (defn arrears-6-wk[event]
     [:div
      [:h4   "Stockport Homes " [:span {:style {:font-weight "normal"}} "6 Week Arrears"]]
      [:div.row {:class "cin"}
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-3
         [:strong "Open Date"]]
        [:div.col.col-md-9
         (:timestamp (fh/unparse-timestamp event))]]]
      (middle-column event)
      (right-column event)]])


  (defn asb[event]
        [:div
         [:h4   "Stockport Homes " [:span {:style {:font-weight "normal"}} "Anti Social Behaviour"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "Individual Id"]]
            [:div.col.col-md-9
             (:individual-id event)]]
           [:div.row
            [:div.col.col-md-3
             [:strong "National Insurance"]]
            [:div.col.col-md-9
             (:nino event)]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Close Date"]]
            [:div.col.col-md-9
             (:date-completed event)]]]
          (middle-column event)
          (right-column event)]])

  (defn eviction-application[event]
        [:div
         [:h4   "Stockport Homes " [:span {:style {:font-weight "normal"}} "Eviction Application"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "National Insurance"]]
            [:div.col.col-md-9 (:nino event)]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]]
          (middle-column event)
          (right-column event)]])

  (defn notice-seeking-possession[event]
        [:div
         [:h4   "Stockport Homes " [:span {:style {:font-weight "normal"}} "Notice Seeking Possesion"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "National Insurance"]]
            [:div.col.col-md-9
             (:nino event)]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]           ]
          (middle-column event)
          (right-column event)]])