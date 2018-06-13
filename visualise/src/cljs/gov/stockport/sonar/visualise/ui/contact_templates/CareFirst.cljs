(ns gov.stockport.sonar.visualise.ui.contact-templates.CareFirst
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [clojure.string :as s]))


(defn- left-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "CareFirst ID"]]
    [:div.col.col-md-8
     (:carefirst-id event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "DOB"]]
    [:div.col.col-md-8
     (fh/to-dob(:dob event))
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Open Date"]]
    [:div.col.col-md-8
     (:timestamp (fh/unparse-timestamp event))
     ]]
   (if (not(s/blank?(:end-date event)))
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Close Date"]]
      [:div.col.col-md-8
       (fh/cf-close-date(:end-date event))
       ]])
   ])


(defn- middle-column [event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-8
     [:strong "Address"]
     ]
    [:div.col.col-9-sm
     (:address event) [:br]
     (:post-code event)
     ]]
   [:div.row
    [:div.col.col-md-8
     [:strong "Address start date"]
     ]
    [:div.col.col-9-sm
     (fh/cf-close-date (:address-start event))
     ]]
   (if (not (s/blank? (:address-end event)))
   [:div.row
    [:div.col.col-md-8
     [:strong "Address end date"]
     ]
    [:div.col.col-9-sm
     (fh/cf-close-date(:address-end event))
     ]])
   ]

  )

(defn contact[event]
  [:div
  [:h4   "Care First " [:span {:style {:font-weight "normal"}} "Contact (adult)"]]
  [:div.row {:class "carefirst contact"}
   (left-column event)
   (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Contact Type"]]
     [:div.col.col-md-8
      "Existing client contact"
      ]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Team name"]]
     [:div.col.col-md-8
      "Adult Contact Centre"]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Worker"]]
     [:div.col.col-md-8
      (:worker-name event) [:br]
      (:worker-tel event)
      ]
    ]
   ]]]
  )


(defn service-agreement[event]
  [:div
  [:h4   "Care First " [:span {:style {:font-weight "normal"}} "Service Agreement"]]
  [:div.row {:class "service-agreement"}
   (left-column event)
   (middle-column event)
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Service Provider"]]
     [:div.col.col-md-8
      (:service-provider event)
      ]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Type"]]
     [:div.col.col-md-8
      (:service-type event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Element"]]
     [:div.col.col-md-8
      (:service-element event) [:br]
      ]
     ]
    [:div.row
     [:div.col.col-md-4
      [:strong "End Reasom"]]
     [:div.col.col-md-8
      (:service-end-reason event) [:br]
      ]
     ]
    ]]])