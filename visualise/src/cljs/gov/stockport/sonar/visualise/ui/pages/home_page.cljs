(ns gov.stockport.sonar.visualise.ui.pages.home-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as sc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn home-page []
  [:div.container-fluid.header
   [:div.row
    [:div.column.col-sm-3.stockport
     [:div.navbar-brand {:href "#"}
      [:img.logo
       {:alt "Stockport MBC",
        :src "/images/stockport_logo.gif"}]]]

    [:div.column.col-sm-9
     [:div.navbar-brand [:i.signpost.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]
      [:div.navbar-brand.title "SIGNPOSTS"]]
     [:div.form
      [:button.logout.btn.btn-primary.pull-right {:type :submit :on-click ac/logout} "Logout"]]]]

   [:div.row.body
    [:div.column.container-criteria.col-sm-3
     [:div.column-title.results-title "SEARCH BY"]
     [sc/search-control !app (h/default-handler !data)]]

    [:div.column.container-results.col-sm-2
     [:div.column-title.results-title "RESULTS"]
     [ic/cards !data]]

    [:div.column.container-timeline.col-sm-7
     [tr/results-tabs !data]]]])