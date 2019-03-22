(ns gov.stockport.sonar.visualise.ui.pages.home-page
  "Home Page of Signposts"
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.state :as st]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as nsc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!data refresh-status!]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]
            [gov.stockport.sonar.visualise.ui.components.welcome-status :refer [welcome-message]]
           ))



(defn results [!data]
  [:div.container-fluid {:id "results"}
   [:div.row.no-gutters
    [:div.col-lg-2.col-md-3.col-sm-4
     [ic/cards !data]]
    [:div.col-lg-10.col-md-9.col-sm-8.results-tab
     [tr/results-tab !data]]]])

(defn home-page []
  (js/setInterval refresh-status! 60000)
  (fn []
  [:div
   [busy/overlay]
    [:div.container-fluid.title
      [:div.row.align-items-center.py-1
     [:div.column.col-lg-1.col-md-2.col-sm-2.col-xs-2
      [:div.row.justify-content-center
       [:i.fa.fa-map-signs.fa-2x]]]
     [:div.column.col-lg-10.col-md-7.col-sm-8.col-xs-6
      [:span.h2.page-title  "SIGNPOSTS" ]]
     [:div.column.col-lg-1.col-md-3.col-sm-2.col-xs-4 {:style{:padding-left  "0"}}
      [:button.btn.btn-primary {:on-click ac/logout} "Logout"]]]]
   [nsc/new-search-control (h/default-handler !data)]
   (if (not-empty (:people @!data))
     [results !data]
     [welcome-message])]))
