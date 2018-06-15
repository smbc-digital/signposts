(ns gov.stockport.sonar.visualise.ui.event-templates.GMP
  "GMP event templates"
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]
  [clojure.string :as str]))

(defn asbo [event]
  [:div.event-details
   [:div.panel-heading
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "ASBO"]]]
   [:div.row {:class "ASBO"}
     [:div.col..col-md-4
         [:div.row
         [:div.col.col-md-6
         [:strong.label "Date issued"]]
         [:div.col.col-md-6
          (:timestamp (fh/unparse-timestamp event))
          ]]
      ]
      [:div.col.col-md-4
        [:div.row
        [:div.col.col-md-6
        [:strong "Address"]
         ]
        [:div.col.col-9-sm
         (:address event)
         ]]
      ]
      [:div.col.col-md-4
       [:div.row
       [:div.col.col-md-4
       [:strong "Postcode"]]
       [:div.col.col-8-sm
       (:postcode event)]]
      ]
    ]]
)


(defn caution [event]
  [:div.event-details
   [:div.panel-heading
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "Caution"]]]
  [:div.row {:class "Caution"}
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Date issued"]]
     [:div.col.col-md-6
      (:timestamp (fh/unparse-timestamp event))
      ]]
    ]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-3
      [:strong "Address"]
      ]
     [:div.col.col-md-6
      (:address event)
      ]]
    ]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-2-sm
      [:strong "Postcode"]]
     [:div.col.col-2-sm
      (:postcode event)]]
    ]
   ]]
  )

(defn domestic [event]
  [:div.event-details
   [:div.panel-heading
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "Domestic Violence"]]]
  [:div.row {:class "gmp domestic"}
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Crime Number"]]
     [:div.col.col-md-6
      (:source-crime-ref event)]]
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Victim Unique Refernce Number"]]
     [:div.col.col-md-6
      (:victim-urn event)]]
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Victim Gender"]]
     [:div.col.col-md-6
      (:gender event)]]
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Open Date"]]
     [:div.col.col-md-6
      (:timestamp (fh/unparse-timestamp event))]]
    [:div.row
     [:div.col.col-md-6
      [:strong.label "Crime Date"]]
     [:div.col.col-md-6
      (:crime-committed-from-full-date event)]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-3
      [:strong "Address"]]
     [:div.col.col-md-9
      (:address event)
      [:br]
      (:postcode event)]]]
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Crime Category"]]
     [:div.col.col-8-sm
      (:ho-crime-category event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Offence Group"]]
     [:div.col.col-8-sm
      (:ho-offence-group event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Outcome"]]
     [:div.col.col-8-sm
      (:crime-outcome-short-description event)]]]]])