(ns visualise.ui.pages.home
  (:require [reagent.core :as r]
            [visualise.common.results.handler :as h]
            [visualise.ui.search.search-control :as sc]
            [visualise.ui.results.raw-table :as rt]
            [visualise.ui.results.timeline-flot :refer [timeline-flot]]
            [visualise.ui.explore :as e]
            [visualise.ui.cards.cards-state :as c]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn home-page []
  [:div.container-fluid.header
   [:div.row
    [:nav.navbar-default {:role "navigation"}
     [:div.container-fluid
      [:div.navbar-header
       [:a.navbar-brand {:href "#"} "Stockport | SoNAR"]]
      [:ul.nav.navbar-nav.navbar-right
       [:li
        [:button.btn.btn-default.navbar-btn
         {:type "button"} "Log out"]]]]]]

   [:div.row.body
    [:div.column.container-criteria.col-md-3
     [:div.column-title "Search by"]
     [sc/search-control !app (h/default-handler !data)]]

    [:div.column.container-results.col-md-2
     [:div.column-title "Results"]
      [c/cards !data]]

    [:div.column.container-timeline.col-md-7
     [:div.column-title "Timeline"]
     [:div
      [timeline-flot !data]
      [rt/raw-table !data]]]]])