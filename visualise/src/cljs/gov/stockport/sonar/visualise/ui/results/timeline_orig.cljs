(ns gov.stockport.sonar.visualise.ui.results.timeline-orig
  (:require [gov.stockport.sonar.visualise.ui.explore :refer [graphit view-state]]))

(defn timeline-orig [!data]
  (fn []
    (let [results (:result @!data)]
      (when (not-empty results)
        [:div.panel.panel-default.criteria-box
         [:div.panel-heading "Timeline Not Flot"]
         [:div.panel-body
          [graphit (view-state) !data]]]))))

