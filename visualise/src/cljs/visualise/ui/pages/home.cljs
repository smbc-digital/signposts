(ns visualise.ui.pages.home
  (:require [reagent.core :as r]
            [visualise.common.results.handler :as h]
            [visualise.ui.search.search-control :as sc]
            [visualise.ui.results.raw-table :as rt]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn home-page []
  [:div.container-fluid
   [:nav
    [:div.col-sm-12
     [:h3 "Stockport | SoNAR"]]]
   [:div.col-sm-4
    [:div.panel.panel-default
     [:div.panel-heading
      [:div.panel-title "search by"]]
     [:div.panel-body
      [sc/search-control !app (h/default-handler !data)]]]]
   [:div.col-sm-8
    [:div.panel.panel-default
     [:div.panel-heading
      [:div.panel-title "results"]]
     [:div.panel-body
      [rt/raw-table !data]]]]])
