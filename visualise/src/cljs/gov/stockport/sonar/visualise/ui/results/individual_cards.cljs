(ns gov.stockport.sonar.visualise.ui.results.individual-cards)

(defn cards [!data]
  (fn []
    (let [individuals (:individuals @!data)]
      (when (not-empty individuals)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " events from " (count individuals) " individuals"]
         (map
           (fn [{:keys [color name dob address postcode]}]
             ^{:key (gensym)}
             [:div.panel.panel-default.card-box
              [:div.panel-heading.card-name {:class color} name]
              [:div.panel-body
               [:p.info-label "Date of Birth: "]
               [:p.info dob]
               [:p.info-label "Address: "]
               [:p.info address]
               [:p.info-label "Postcode: "]
               [:p.info postcode]]])
           individuals)]))))

