(ns gov.stockport.sonar.visualise.ui.results.individual-cards)

(defn cards [!data]
  (fn []
    (let [individuals (:individuals @!data)]
      (when (not-empty individuals)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event" (if (> (:total @!data) 1) "s") " from " (count individuals) " individual" (if (> (count individuals) 1) "s")]
         (map
           (fn [{:keys [color name dob address]}]
             ^{:key (gensym)}
             [:div.panel.panel-default.card-box {:class color}
              [:div.panel-heading.card-name]
              [:div.panel-body
               [:p.info name]
               [:p.info-label "Date of Birth: "]
               [:p.info dob]
               [:p.info-label "Address: "]
               [:p.info address]]])
           individuals)]))))

