(ns visualise.ui.cards.cards-state
  (:require [visualise.common.results.individuals :as i]
            [visualise.common.ui.flot-data :refer [colors]]))

(defn cards [!data]
  (fn []
    (let [results (:result @!data)]
      (when (not-empty results)
          [:div
           (map
             (fn [{:keys [idx name dob address]}]
               ^{:key (gensym)}
               [:div.panel.panel-default
                [:div.panel-heading {:class (get colors idx)} name]
                [:div.panel-body
                 [:p dob]
                 [:p address]]]) (i/individuals results))]))))

