(ns gov.stockport.sonar.visualise.ui.event-templates.RevsBens
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh ])
  )

  (defn- left-column [event]
    [:div.col..col-4-sm
     [:div.row
      [:div.col.col-1-sm
       [:strong.label "Council Tax ID"]]
      [:div.col-3-sm
       (:eis-number event)
       ]]
     [:div.row
      [:div.col.col-1-sm
       [:strong.label "National Insurance"]]
      [:div.col-3-sm
       (:ni-number event)
       ]]
     [:div.row
      [:div.col.col-1-sm
       [:strong.label "Open Date"]]
      [:div.col-3-sm
       (:timestamp (fh/unparse-timestamp event))
       ]]
     [:div.row
      [:div.col.col-1-sm
       [:strong.label "Dependents"]]
      [:div.col-3-sm
       (:non-dependents event)
       ]]
     [:div.row
      [:div.col.col-1-sm
       [:strong.label "Non Dependents"]]
      [:div.col-3-sm
       (:number-of-non-dependents event)
       ]]
     ])


(defn- middle-column[event]
  [:div.col.col-4-sm
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address"]
     ]
    [:div.col.col-9-sm
     (:address event) [:br]
     (:postcode event)
     ]]
   ]
  )


(defn ct-support[event]
    [:div
     [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Council Tax Support"]]
     [:div.row {:class "Yos"}
      (left-column event)
      (middle-column event)
      [:div.col.col-4-sm
       [:div.row
        [:div.col.col-3-sm
         [:strong "Benefit Type"]
         ]
        [:div.col.col-9-sm
         (:benefit-type event)
         ]]
       [:div.row
        [:div.col.col-3-sm
         [:strong "Claim status"]
         ]
        [:div.col.col-9-sm
         (:ct-claim-status event)
         ]]
       [:div.row
        [:div.col.col-3-sm
         [:strong "Tenancy Type"]
         ]
        [:div.col.col-9-sm
         (:tenancy-type event)
         ]]
       ]]])

(defn ctax-bill[event]
  [:div
   [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Council Tax Bill"]]
   [:div.row {:class "ctax-bill"}
   [:div.col..col-4-sm
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Other Name on Bill"]]
     [:div.col-3-sm
      (:other-name-on-bill event)
      ]]
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Event Logged"]]
     [:div.col-3-sm
      (:timestamp event)
      ]]
    ]
    (middle-column event)
    [:div.col.col-4-sm
     ]
]])


(defn hb-cts[event]
  [:div
   [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Housing Benefit and Council Tax Support"]]
   [:div.row {:class "Yos"}
    (left-column event)
    (middle-column event)
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-3-sm
       [:strong "Benefit Type"]
       ]
      [:div.col.col-9-sm
       (:benefit-type event)
       ]]
     [:div.row
      [:div.col.col-3-sm
       [:strong "Claim status"]
       ]
      [:div.col.col-9-sm
       (:hb-claim-status event)
       ]]
     [:div.row
      [:div.col.col-3-sm
       [:strong "Tenancy Type"]
       ]
      [:div.col.col-9-sm
       (:tenancy-type event)
       ]]
     ]]])

