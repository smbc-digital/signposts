(ns gov.stockport.sonar.visualise.ui.results.selected-event
  (:require
    [gov.stockport.sonar.visualise.ui.event-templates.template-map :as tm]))

(defn selected-event [!data]
  (fn []
    (let [selected (:selected-event @!data)]
      (when (not-empty selected)
        [:div.selected-event
         ((tm/get-template selected) selected)
         ]))))