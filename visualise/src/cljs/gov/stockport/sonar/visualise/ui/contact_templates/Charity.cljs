(ns gov.stockport.sonar.visualise.ui.contact-templates.Charity
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [clojure.string :as s]
  ))



    (defn- left-column[event]
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-4
         [:strong.label "Diary Reference Number"]]
        [:div.col.col-md-8
         (:diary-reference-number event)
         ]]
       [:div.row
        [:div.col.col-md-4
         [:strong.label "Referral Date"]]
        [:div.col.col-md-8
         (:timestamp (fh/unparse-timestamp event))
         ]]
       [:div.row
        [:div.col.col-md-4
         [:strong.label "Closure Date"]]
        [:div.col.col-md-8
         (:closure-date event)
         ]]])



  (defn- middle-column [event]
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-3
       [:strong "Address"]
       ]
      [:div.col.col-md-9
       (:address event)
       [:br]
       (:postcode event)
       ]]
     ])




  (defn prevention-alliance[event]
    [:div
     [:h4   "Charity Log " [:span {:style {:font-weight "normal"}} "Prevention Alliance"]]
     [:div.row {:class "charity-log"}
      (left-column event)
      (middle-column event)
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-4
         [:strong "Project"]]
        [:div.col.col-md-8
         (:project event)]]
       [:div.row
        [:div.col.col-md-4
         [:strong "Sub Category"]]
        [:div.col.col-md-8
         (:subcategory event)]]
       [:div.row
        [:div.col.col-md-4
         [:strong "Referrer"]]
        [:div.col.col-md-8
         (:referrer event)]]
       [:div.row
        [:div.col.col-md-4
         [:strong "Org Person ID"]]
        [:div.col.col-md-8
         (:org-person-id event)]]
       [:div.row
        [:div.col.col-md-4
         [:strong "Report Person Type"]]
        [:div.col.col-md-8
         (:report-person-type event)]]
       ]
      ]]

        )