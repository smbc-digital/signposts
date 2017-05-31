(ns gov.stockport.sonar.visualise.ui.pages.login-page
  (:require [gov.stockport.sonar.visualise.ui.login-form :refer [login-form]]
            [gov.stockport.sonar.visualise.state :refer [!app]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]))

(defn login-page []
  [:div
   [busy/overlay]
   [:div.container-fluid
    {:style {:background-color "#1c3645" :color :white}}
    [:div.row.align-items-center.py-1
     [:div.column.col-1
      [:div.row.justify-content-center
       [:i.fa.fa-map-signs.fa-2x]]]
     [:div.column.col-11
      [:span.h2 "SIGNPOSTS"]]]]

    [:div.container
     [:div.column.container-criteria.col-2]
     [:div.column.col-10
      [login-form]]]])