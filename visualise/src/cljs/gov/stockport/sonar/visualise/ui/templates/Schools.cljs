(ns gov.stockport.sonar.visualise.ui.templates.Schools
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- left-column [event]
  [:div.col..col-4-sm
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "EIS Number"]]
    [:div.col-3-sm
     (:eis-number event)
     ]]
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Unique Pupil ID"]]
    [:div.col-3-sm
     (:unique-pupil-id event)
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
     [:strong "School"]
     ]
    [:div.col.col-9-sm
     (:school-name event)[:br]
     (:school-district event) [:br]
     (:school-type event)[:br]
     (:school-phone event) [:br]
     (:school-email event) [:br]
     (:school-website event)
     ]]
   ])

(defn attendance[event]
  [:div
  [:h4   "Schools " [:span {:style {:font-weight "normal"}} "Attendance"]]
  [:div.row {:class "cin"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Presenting Issue"]]
     [:div.col.col-8-sm
      (:primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Outcome"]]
     [:div.col.col-8-sm
      "Proceed to Social Care Referral"]]
    ]
   ]]
  )

(defn exclusions[event]
  [:div
   [:h4   "Schools " [:span {:style {:font-weight "normal"}} "Exclusions"]]
   [:div.row {:class "cin"}
    (left-column event)
    (middle-column event)
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-4-sm
       [:strong "Presenting Issue"]]
      [:div.col.col-8-sm
       (:primary-presenting-issue event)]]
     [:div.row
      [:div.col.col-4-sm
       [:strong "Outcome"]]
      [:div.col.col-8-sm
       "Proceed to Social Care Referral"]]
     ]
    ]]
  )

(defn registrations[event]
  [:div
   [:h4   "Schools " [:span {:style {:font-weight "normal"}} "Registrations"]]
   [:div.row {:class "cin"}
    (left-column(event))
    (middle-column(event))
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-4-sm
       [:strong "Presenting Issue"]]
      [:div.col.col-8-sm
       (:primary-presenting-issue event)]]
     [:div.row
      [:div.col.col-4-sm
       [:strong "Outcome"]]
      [:div.col.col-8-sm
       "Proceed to Social Care Referral"]]
     ]
    ]]
  )