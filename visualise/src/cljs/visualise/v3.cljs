(ns visualise.v3
  (:require [visualise.ui.explore :refer [graph view-state]]
            [visualise.aggregation.data :refer [random-events]]
            [visualise.aggregation.aggregation :refer [aggregate-and-group]]))

(defn home-page []
  (let [event-series (random-events 5)]
    (fn []
      [:div
       [graph (view-state {:zoom 1}) event-series]
       [graph (view-state {:zoom 2}) event-series]
       ])))