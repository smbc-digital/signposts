(ns gov.stockport.sonar.visualise.ui.pages.home-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.new-search-control :as nsc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!app !data]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.ui.components.welcome-status :refer [welcome-message]]))

(defn results [!data]
  [:div
   [:div.container-fluid
    {:style {:background-color "#1d2932"}}
    [:h6.text-white.pb-1 (people/results-summary @!data)]]

   [:div.container-fluid
    [:div.row.no-gutters
     [:div.col-3
      [ic/cards !data]]

     [:div.col-9
      [tr/results-tabs !data]]]]])


(defn home-page []
  [:div
   [busy/overlay]
   [:div.container-fluid
    {:style {:background-color "#1c3645" :color :white}}
    [:div.row.align-items-center.py-1
     [:div.column.col-1
      [:div.row.justify-content-center
       [:i.fa.fa-map-signs.fa-2x]]]
     [:div.column.col-10
      [:span.h2 "SIGNPOSTS"]]
     [:div.column.col-1
      [:button.btn.btn-primary {:on-click ac/logout} "Logout"]]]]

   [nsc/new-search-control (h/default-handler !data)]

   (if (not-empty (:result @!data))
     [results !data]
     [welcome-message])])