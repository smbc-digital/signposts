(ns gov.stockport.sonar.visualise.ui.event-templates.EIS
  (:require [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [clojure.string :as s]))

(defn- primary-presenting-issue[event]
  (let [issue(:primary-presenting-issue event)]
      (s/replace issue #"[{}]" "")
    ))

(defn- left-column[event]
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
     (:unique-pupil-number event)
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
   ]
  )

(defn- middle-column[event]
  [:div.col.col-4-sm
   [:div.row
    [:div.col.col-3-sm
     [:strong "Address"]
     ]
    [:div.col.col-9-sm
     (:address event)"," [:br]
     (:postcode event)
     ]]
   ]
  )

(defn contact [event]
  [:div
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Contact"]]
  [:div.row {:class "eis-in"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Presenting Issue"]]
     [:div.col.col-8-sm
      (primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Outcome"]]
     [:div.col.col-8-sm
      "Proceed to Social Care Referral"]]
    ]
   ]]
  )

(defn cin [event]
  [:div
  [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Child in Need"]]
  [:div.row {:class "cin"}
   (left-column event)
   (middle-column event)
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Presenting Issue"]]
     [:div.col.col-8-sm
      (primary-presenting-issue event)]]
    [:div.row
     [:div.col.col-4-sm
      [:strong "Closure reason"]]
     [:div.col.col-8-sm
      (:closure event)]]
    ]
   ]]
  )

(defn lac [event]
  [:div
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Looked-after child"]]
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
      [:strong "Closure reason"]]
     [:div.col.col-8-sm
      (:closure-reason event)]]
    ]
    ]]
  )

(defn sen [event]
  [:div
   [:h4   "EIS " [:span {:style {:font-weight "normal"}} "Special Educational Needs"]]
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
      [:strong "Ongoing"]]
     [:div.col.col-8-sm
      (:ongoing event)]]
    ]
   ]]
  )