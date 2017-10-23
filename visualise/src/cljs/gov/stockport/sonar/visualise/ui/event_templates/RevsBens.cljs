(ns gov.stockport.sonar.visualise.ui.event-templates.RevsBens
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh ]
            [clojure.string :as s]
            ))

  (defn- left-column [event]
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Name"]]
      [:div.col.col-md-8
       (:name event)
       ]]
      [:div.row
       [:div.col.col-md-4
        [:strong.label "DOB"]]
       [:div.col.col-md-8
        (fh/to-dob(:dob event))
        ]]
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Council Tax ID"]]
      [:div.col.col-md-8
       (:council-tax-id event)
       ]]
     (if (some? (:housing-benefit-id event))
       [:div.row
        [:div.col.col-md-4
         [:strong.label "Housing Benefit ID"]]
        [:div.col.col-md-8
         (:housing-benefit-id event)
         ]]

       )
     [:div.row
      [:div.col.col-md-4
       [:strong.label "National Insurance"]]
      [:div.col.col-md-8
       (:ni-number event)
       ]]
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Open Date"]]
      [:div.col.col-md-8
       (:timestamp (fh/unparse-timestamp event))
       ]]
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Dependents"]]
      [:div.col.col-md-8
       (:non-dependents event)
       ]]
     [:div.row
      [:div.col.col-md-4
       [:strong.label "Non Dependents"]]
      [:div.col.col-md-8
       (:number-of-non-dependents event)
       ]]
     ])

(defn- middle-column[event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-3
     [:strong "Address"]
     ]
    [:div.col.col-md-9
     (:address event) [:br]
     (:postcode event)
     ]]
   ]
  )

(defn ct-support[event]
    [:div.event-details
     [:div.panel-heading
     [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Council Tax Support"]]]
     [:div.row {:class "Yos"}
      (left-column event)
      (middle-column event)
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-4
         [:strong "Benefit Type"]
         ]
        [:div.col-md-8
         (:benefit-type event)
         ]]
       [:div.row
        [:div.col-md-4
         [:strong "Claim status"]
         ]
        [:div.col-md-8
         (:ctb-claim-status event)
         ]]
       [:div.row
        [:div.col.col-md-4
         [:strong "Tenancy Type"]
         ]
        [:div.col.col-md-8
         (:tenancy-type event)
         ]]
       ]]])

(defn ctax-bill[event]
  [:div.event-details
   [:div.panel-heading
   [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Council Tax Bill"]]]
   [:div.row {:class "ctax-bill"}
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Name"]]
     [:div.col.col-md-8
      (:name event)
      ]]
    [:div.row
     [:div.col.col-md-4
      [:strong.label "Event Logged"]]
     [:div.col.col-md-8
      (:timestamp (fh/unparse-timestamp event))
      ]]
    ]
    (middle-column event)
    [:div.col.col-md-4
     ]
]])

(defn hb-cts[event]
  [:div.event-details
   [:div.panel-heading
   [:h4   "RevsBens " [:span {:style {:font-weight "normal"}} "Housing Benefit and Council Tax Support"]]]
   [:div.row {:class "Yos"}
    (left-column event)
    (middle-column event)
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-4
       [:strong "Benefit Type"]
       ]
      [:div.col.col-md-8
       (:benefit-type event)
       ]]
     [:div.row
      [:div.col.col-md-4
       [:strong "HB Claim status"]
       ]
      [:div.col.col-md-8
       (:hb-claim-status event)
       ]]
     [:div.col.col-md-4
      [:strong "CTS Claim status"]
      ]
     [:div.col.col-md-8
      (:ctb-claim-status event)
      ]]
     [:div.row
      [:div.col.col-md-4
       [:strong "Tenancy Type"]
       ]
      [:div.col.col-md-8
       (:tenancy-type event)
       ]]
     ]])