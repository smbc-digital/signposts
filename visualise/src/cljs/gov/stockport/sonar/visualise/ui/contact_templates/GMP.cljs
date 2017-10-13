(ns gov.stockport.sonar.visualise.ui.templates.GMP
  (:require
  [gov.stockport.sonar.visualise.util.fmt-help :as fh]
  [clojure.string :as str]
  ))



(defn asbo [event]
  [:div
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "ASBO"]]
   [:div.row {:class "ASBO"}
     [:div.col..col-4-sm
         [:div.row
         [:div.col.col-1-sm
         [:strong.label "Date issued"]]
         [:div.col-3-sm
          (:timestamp (fh/unparse-timestamp event))
          ]]
      ]
      [:div.col.col-4-sm
        [:div.row
        [:div.col.col-3-sm
        [:strong "Address"]
         ]
        [:div.col.col-9-sm
         (:address event)
         ]]
      ]
      [:div.col.col-4-sm
       [:div.row
       [:div.col.col-4-sm
       [:strong "Postcode"]]
       [:div.col.col-8-sm
       (:postcode event)]]
      ]
    ]]
)


(defn caution [event]
  [:div
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "Caution"]]
  [:div.row {:class "Caution"}
   [:div.col..col-4-sm
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Date issued"]]
     [:div.col.col-3-sm
      (:timestamp (fh/unparse-timestamp event))
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-1-sm
      [:strong "Address"]
      ]
     [:div.col.col-3-sm
      (:address event)
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-2-sm
      [:strong "Postcode"]]
     [:div.col.col-2-sm
      (:postcode event)]]
    ]
   ]]
  )

(defn domestic [event]
  [:div
  [:h4   "Police " [:span {:style {:font-weight "normal"}} "Domestic Violence"]]
  [:div.row {:class "gmp domestic"}
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Crime Number"]]
     [:div.col-3-sm
      (:source-crime-ref event)
      ]]
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Victim Unique Refernce Number"]]
     [:div.col-3-sm
      (:victim-urn event)
      ]]
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Victim Gender"]]
     [:div.col-3-sm
      (:victim-gender event)
      ]]
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Open Date"]]
     [:div.col-3-sm
      (:timestamp (fh/unparse-timestamp event))
      ]]
    [:div.row
     [:div.col.col-1-sm
      [:strong.label "Crime Date"]]
     [:div.col-3-sm
      (:crime-commited-from-full-date event)
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-3-sm
      [:strong "Address"]
      ]
     [:div.col.col-9-sm
      (:address event)
      [:br]
      (:postcode event)
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Crime Category"]]
     [:div.col.col-8-sm
      (:ho-crime-category event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Offence Group"]]
     [:div.col.col-8-sm
      (:ho-offence-group event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Outcome"]]
     [:div.col.col-8-sm
      (:crime-outcome-short-description event)]]
    ]
   ]])