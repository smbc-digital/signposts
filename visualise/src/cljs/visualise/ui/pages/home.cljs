(ns visualise.ui.pages.home
  (:require [reagent.core :as r]
            [visualise.query.handler :as h]
            [visualise.ui.search.search-control :as sc]
            [visualise.ui.results.tabbed-results :as tr]
            [visualise.ui.results.individual-cards :as ic]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn home-page []
  [:div.container-fluid.header
   [:div.row
    [:nav.navbar-default {:role "navigation"}
     [:div.container-fluid
      [:div.navbar-header.stockport.col-md-3
       [:a.navbar-brand {:href "#"} "Stockport MBC"]]
      [:div.navbar-header
       [:a.navbar-brand {:href "#"} "SIGNPOSTS"]]
      ]]]

   [:div.row.body
    [:div.column.container-criteria.col-md-3
     [:div.column-title.search-by "SEARCH BY"]
     [sc/search-control !app (h/default-handler !data)]]

    [:div.column.container-results.col-md-2
     [:div.column-title "RESULTS"]
     [ic/cards !data]]

    [:div.column.container-timeline.col-md-7
     [tr/results-tabs !data]]]])