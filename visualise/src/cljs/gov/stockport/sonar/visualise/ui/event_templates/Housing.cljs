(ns gov.stockport.sonar.visualise.ui.event-templates.Homes
  (:require
    [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

  (defn- middle-column[event]
           [:div.col.col-4-sm
            [:div.row
             [:div.col.col-3-sm
              [:strong "Address"]
              ]
             [:div.col.col-9-sm
              (:address event)
              ]]])

  (defn- right-column[event]
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-3-sm
       [:strong "Key worker"]
       ]
      [:div.col.col-9-sm
       (:keyworker event)
       ]]
     (when(some? (:otherinfo event ))
     [:div.row
      [:div.col.col-3-sm
       [:strong "Other Information"]
       ]
      [:div.col.col-9-sm
       (:otherinfo event)
       ]])])

  (defn arrears-6-wk[event]
     [:div
      [:h4   "Homes " [:span {:style {:font-weight "normal"}} "6 Week Arrears"]]
      [:div.row {:class "cin"}
      [:div.col.col-4-sm
       [:div.row
        [:div.col.col-3-sm
         [:strong "Open Date"]
         ]
        [:div.col.col-9-sm
         (:timestamp (fh/unparse-timestamp event))
         ]]
       ]
      (middle-column event)
      (right-column event)
      ]])


  (defn asb[event]
        [:div
         [:h4   "Homes " [:span {:style {:font-weight "normal"}} "Anti Social Behaviour"]]
         [:div.row {:class "cin"}
          [:div.col.col-4-sm
           [:div.row
            [:div.col.col-3-sm
             [:strong "Individual Id"]
             ]
            [:div.col.col-9-sm
             (:individual-id event)
             ]]
           [:div.row
            [:div.col.col-3-sm
             [:strong "National Insurance"]
             ]
            [:div.col.col-9-sm
             (:nino event)
             ]]
           [:div.row
            [:div.col.col-3-sm
             [:strong "Open Date"]
             ]
            [:div.col.col-9-sm
             (:timestamp (fh/unparse-timestamp event))
             ]]
           [:div.row
            [:div.col.col-3-sm
             [:strong "Close Date"]
             ]
            [:div.col.col-9-sm
             (:date-completed event)
             ]]
           ]
          (middle-column event)
          (right-column event)
          ]])



  (defn eviction-application[event]
        [:div
         [:h4   "Homes " [:span {:style {:font-weight "normal"}} "Eviction Application"]]
         [:div.row {:class "cin"}
          [:div.col.col-4-sm
           [:div.row
            [:div.col.col-3-sm
             [:strong "National Insurance"]
             ]
            [:div.col.col-9-sm
             (:nino event)
             ]]
           [:div.row
            [:div.col.col-3-sm
             [:strong "Open Date"]
             ]
            [:div.col.col-9-sm
             (:timestamp (fh/unparse-timestamp event))
             ]]
           ]
          (middle-column event)
          (right-column event)
          ]])


  (defn notice-seeking-posession[event]
        [:div
         [:h4   "Homes " [:span {:style {:font-weight "normal"}} "Notice Seeking Possesion"]]
         [:div.row {:class "cin"}
          [:div.col.col-4-sm
           [:div.row
            [:div.col.col-3-sm
             [:strong "National Insurance"]
             ]
            [:div.col.col-9-sm
             (:nino event)
             ]]
           [:div.row
            [:div.col.col-3-sm
             [:strong "Open Date"]
             ]
            [:div.col.col-9-sm
             (:timestamp (fh/unparse-timestamp event))
             ]]
           ]
          (middle-column event)
          (right-column event)
          ]])
