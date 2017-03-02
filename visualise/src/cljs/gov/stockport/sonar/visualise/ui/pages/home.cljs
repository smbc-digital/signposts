(ns gov.stockport.sonar.visualise.ui.pages.home
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as sc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn home-page []
  [:div.container-fluid.header
   [:div.row
    [:div.column.col-sm-3.stockport
     [:a.navbar-brand {:href "#"} "Stockport MBC"]]
    [:div.column.col-sm-9
     [:a.navbar-brand {:href "#"} "SIGNPOSTS"]]]

   [:div.row.body
    [:div.column.container-criteria.col-sm-3
     [:div.column-title.search-by "SEARCH BY"]
     [sc/search-control !app (h/default-handler !data)]]

    [:div.column.container-results.col-sm-2
     [:div.column-title "RESULTS"]
     [ic/cards !data]]

    [:div.column.container-timeline.col-sm-7
     [tr/results-tabs !data]]]])