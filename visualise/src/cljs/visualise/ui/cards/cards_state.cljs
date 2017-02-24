(ns visualise.ui.cards.cards-state
  (:require [visualise.common.results.individuals :as i]))

(defn cards [!data]
  (fn []
    (let [results (:result @!data)]
      (when (not-empty results)
          [:div
           (map
             (fn [{:keys [name dob address]}]
               ^{:key (gensym)}
               [:div.panel.panel-default
                [:div.panel-heading name]
                [:div.panel-body
                 [:p "DOB: " dob]
                 [:p "ADDRESS: " address]]]) (i/individuals results))]))))

