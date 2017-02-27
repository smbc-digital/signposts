(ns visualise.ui.results.individual-cards)

(defn cards [!data]
  (fn []
    (let [individuals (:individuals @!data)]
      (when (not-empty individuals)
        [:div.cards
         (map
           (fn [{:keys [color name dob address]}]
             ^{:key (gensym)}
             [:div.panel.panel-default
              [:div.panel-heading {:class color} name]
              [:div.panel-body
               [:p dob]
               [:p address]]])
           individuals)]))))

