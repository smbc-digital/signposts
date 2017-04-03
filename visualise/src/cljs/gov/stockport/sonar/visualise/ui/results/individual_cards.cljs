(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [gov.stockport.sonar.visualise.data.people :as people]))

(defn cards [!data]
  (fn []
    (let [people (people/by-rank @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event" (if (> (:total @!data) 1) "s") " from " (count people) " individual" (if (> (count people) 1) "s")]
         (map
           (fn [[{:keys [name dob address] :as pkey} {:keys [color display]}]]
             ^{:key (gensym)}
             [:div.panel.panel-default.card-box {:class color}
              [:div.panel-heading.card-name]
              [:div.panel-body
               [:i.fa.fa-2x.pull-right
                {:class    (if display "fa-eye" "fa-eye-slash")
                 :title    (str (if display "Hide" "Show") " this person on the graph")
                 :on-click #(swap! !data update-in [:people pkey :display] not)}]
               [:i.fa.fa-crosshairs.fa-2x.pull-right
                {:title    "Show just this person on the graph"
                 :on-click #(swap! !data people/focus-on pkey)}]
               [:p.info name]
               [:p.info-label "Date of Birth: "]
               [:p.info dob]
               [:p.info-label "Address: "]
               [:p.info address]]])
           people)]))))

