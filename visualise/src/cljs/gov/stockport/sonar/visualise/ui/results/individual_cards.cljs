(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

(def age (fn [dob]
           (let [years (t/in-years (t/->Interval (f/parse (f/formatter "YYYY-mm-dd") dob) (t/now)))]
             (str " (" years " yrs)"))))



(defn cards [!data]
  (fn []
    (let [people (people/by-rank @!data)
          focused? (:focused @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event" (if (> (:total @!data) 1) "s") " from " (count people) " individual" (if (> (count people) 1) "s")]
         (map
           (fn [[{:keys [name dob address] :as pkey} {:keys [color display focus]}]]
             ^{:key (gensym)}
             [:div.panel.panel-default.card-box {:class (str (cljs.core/name color) (if focused? (if (or focus display) " focus" " blur")))}
              [:div.panel-heading.card-name]
              [:div.panel-body

               [:i.fa.fa-2x.pull-right
                {:class    (if display "fa-toggle-on" "fa-toggle-off")
                 :title    (str (if display "Hide" "Show") " this person on the graph")
                 :on-click #(swap! !data update-in [:people pkey :display] not)}]

               [:i.fa.fa-2x.pull-right
                {:class    (if focus "fa-star" "fa-star-o")
                 :title    (str "Show" (if focus " all people " " only this person ") "on the graph")
                 :on-click #(swap! !data people/focus-on pkey)}]

               [:p.info name (age dob)]
               [:p.info-label "Date of Birth: "]
               [:p.info dob]
               [:p.info-label "Address: "]
               [:p.info address]]])
           people)]))))

