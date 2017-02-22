(ns visualise.ui.pages.home
  (:require [reagent.core :as r]
            [visualise.common.results.handler :as h]
            [visualise.ui.search.search-control :as sc]
            [visualise.ui.results.raw-table :as rt]))

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
     ]

    [:div.column.container-timeline.col-md-7
      [rt/raw-table !data]]]])
