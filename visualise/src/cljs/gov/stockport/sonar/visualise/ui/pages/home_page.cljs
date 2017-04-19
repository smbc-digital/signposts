(ns gov.stockport.sonar.visualise.ui.pages.home-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as sc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!app !data]]))

(defn results [!data]
  (when (not-empty (:result @!data))
    [:div
     [:div.column.container-results.col-sm-3
      [:div.column-title.results-title "RESULTS"]
      [ic/cards !data]]

     [:div.column.container-timeline.col-sm-7
      [tr/results-tabs !data]]]))

(defn search-in-progress []
  (when (:search-in-progress @!app)
    [:div.busy
     [:div.spinner
      [:i.fa.fa-spin.fa-refresh.fa-5x]]]))

(defn home-page []
  [:div.container-fluid.header
   [search-in-progress]
   [:div.row
    [:div.column.col-sm-2.stockport
     [:div.navbar-brand {:href "#"}
      [:img.logo
       {:alt "Stockport MBC",
        :src "/images/stockport_logo.gif"}]]]

    [:div.column.col-sm-10
     [:div.navbar-brand [:i.signpost.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]
      [:div.navbar-brand.title "SIGNPOSTS"]]
     [:div.form
      [:button.logout.btn.btn-primary.pull-right {:type :submit :on-click ac/logout} "Logout"]]]]

   [:div.row.body
    [:div.column.container-criteria.col-sm-2
     [:div.column-title.results-title "SEARCH BY"]
     [sc/search-control !app (h/default-handler !data)]]
    [results !data]]])