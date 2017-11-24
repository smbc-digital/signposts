(ns gov.stockport.sonar.visualise.ui.contact-templates.Schools
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [clojure.string :as s]))

(defn- left-column [event]
  [:div.col.col-md-4
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Pupil ID"]]
    [:div.col.col-md-8
     (:pupil-id event)(:stud-id event)
     ]]
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Unique Pupil ID"]]
    [:div.col.col-md-8
     (:unique-pupil-id event)
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
   (if (not (s/blank? (:end-date event)))
   [:div.row
    [:div.col.col-md-4
     [:strong.label "Close Date"]]
    [:div.col.col-md-8
     (:end-date event)
     ]])
   ])

(defn- middle-column [event]
  [:div.col.col-md-4
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
   [:div.col.col-md-4
    [:div.row
     [:div.col.col-md-4
      [:strong "Academic Year"]]
     [:div.col.col-md-8
      (:academic-year event)]]
    [:div.row
     [:div.col.col-md-4
      [:strong "Average Attendance"]]
     [:div.col.col-md-8
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
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-4
       [:strong "Category"]]
      [:div.col.col-md-8
       (:category event)]]
     [:div.row
      [:div.col.col-md-4
       [:strong "Reason"]]
      [:div.col.col-md-8
       (:reason event)]]
     [:div.row
      [:div.col.col-md-4
       [:strong "Address"]]
      [:div.col.col-md-8
       (:address event) [:br]
       (:postcode event) [:br]
       (:daytime-telephone event)
       ]]
     ]
    ]]
  )

(defn registrations[event]
  [:div
   [:h4 "Schools " [:span {:style {:font-weight "normal"}} "Registrations"]]
   [:div.row {:class "school-registrations"}
    (left-column event)
    (middle-column event)
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-4
       [:strong "Address"]]
      [:div.col.col-md-8
       (:address event) [:br]
       (:postcode event)
       (:daytime-telephone event)
       ]]
     ]]])