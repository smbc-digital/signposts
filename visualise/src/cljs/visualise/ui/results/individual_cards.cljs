(ns visualise.ui.results.individual-cards)

(defn cards [!data]
  (fn []
    (let [individuals (:individuals @!data)]
      (when (not-empty individuals)
        [:div.cards
         (map
           (fn [{:keys [idx name dob address]}]
             ^{:key (gensym)}
             [:div.panel.panel-default
              [:div.panel-heading name]
              [:div.panel-body
               [:p dob]
               [:p address]]])
           individuals)]))))

