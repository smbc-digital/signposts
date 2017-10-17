(ns gov.stockport.sonar.visualise.ui.event-templates.Schools
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

(defn- left-column [event]
  [:div.col..col-4-sm
   [:div.row
    [:div.col.col-1-sm
     [:strong.label "Pupil ID"]]
    [:div.col-3-sm
     (:pupil-id event)(:stud-id event)
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
  [:div.row {:class "schools-attendance"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Academic Year"]]
     [:div.col.col-8-sm
      (:academic-year event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Average Attendance"]]
     [:div.col.col-8-sm
      (:average-attendance event)]]
    ]
   ]]
  )

(defn exclusions[event]
  [:div
   [:h4   "School " [:span {:style {:font-weight "normal"}} "Exclusions"]]
   [:div.row {:class "school-exclusions"}
    (left-column event)
    (middle-column event)
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-4-sm
       [:strong "Category"]]
      [:div.col.col-8-sm
       (:category event)]]
     [:div.row
      [:div.col.col-4-sm
       [:strong "Reason"]]
      [:div.col.col-8-sm
       (:reason event)]]
     [:div.row
      [:div.col.col-4-sm
       [:strong "Address"]]
      [:div.col.col-8-sm
       (:address event) [:br]
       (:postcode event) [:br]
       (:daytime-telephone event)
       ]]
     ]
    ]]
  )

(defn registrations[event]
  [:div
   [:h4   "Schools " [:span {:style {:font-weight "normal"}} "Registrations"]]
   [:div.row {:class "school-registrations"}
    (left-column(event))
    (middle-column(event))
    [:div.col.col-4-sm
     [:div.row
      [:div.col.col-4-sm
       [:strong "Address"]]
      [:div.col.col-8-sm
       (:address event) [:br]
       (:postcode event) [:br]
       ]]
     ]
    ]]
  )