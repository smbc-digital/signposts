(ns gov.stockport.sonar.visualise.ui.templates.CareFirst
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]))


(defn- left-column[event]
  [:div.col..col-4-sm
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "CareFirst ID"]]
    [:div.col-3-sm
     (:carefirst-id event)
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
     (:end-date event)
     ]]
   ])


(defn- middle-column [event]
  [:div.col.col-4-sm
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address"]
     ]
    [:div.col.col-9-sm
     (:address event) [:br]
     (:post-code event)
     ]]
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address start date"]
     ]
    [:div.col.col-9-sm
     (:address-start event)
     ]]
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address end date"]
     ]
    [:div.col.col-9-sm
     (:address-end event)
     ]]
   ]

  )

(defn contact[event]
  [:div
  [:h4   "Care First " [:span {:style {:font-weight "normal"}} "Contact (adult)"]]
  [:div.row {:class "carefirst contact"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Contact Type"]]
     [:div.col.col-8-sm
      "Existing client contact"
      ]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Team name"]]
     [:div.col.col-8-sm
      "Adult Contact Centre"]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Worker"]]
     [:div.col.col-8-sm
      (:worker-name event) [:br]
      (:worker-tel event)
      ]
    ]
   ]]]
  )


(defn service-agreement[event]
  [:div
  [:h4   "Care First " [:span {:style {:font-weight "normal"}} "Service Agreement"]]
  [:div.row {:class "carefirst contact"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Service Provider"]]
     [:div.col.col-8-sm
      (:service-provider event)
      ]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Type"]]
     [:div.col.col-8-sm
      (:service-type event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Element"]]
     [:div.col.col-8-sm
      (:service-element event) [:br]
      ]
     ]
    [:div.row
     [:div.col.col-4-sm
      [:strong "End Reasom"]]
     [:div.col.col-8-sm
      (:service-end-reason event) [:br]
      ]
     ]
    ]]])