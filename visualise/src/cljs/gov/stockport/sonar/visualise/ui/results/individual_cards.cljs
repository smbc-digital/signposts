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
          display-all? (:display-all? @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event"
          (if (> (:total @!data) 1) "s") " from " (count people) " individual" (if (> (count people) 1) "s")]

         [:div.panel.panel-default.card-box
          [:div.panel-body
           [:i.fa.fa-2x.pull-right
            {:class    (if display-all? "fa-toggle-on" "fa-toggle-off")
             :title    (str (if display-all? "Hide" "Show") " all people on the graph")
             :on-click #(swap! !data people/toggle-display-all)}]
           [:p.info (if display-all? "Hide everyone" "Show everyone")]]]

         (map
           (fn [[{:keys [name dob address] :as pkey} {:keys [color display]}]]
             ^{:key (gensym)}
             [:div.panel.panel-default.card-box
              {:class (str (cljs.core/name color)
                           (if display " focus" " blur"))}
              [:div.panel-heading.card-name]
              [:div.panel-body

               [:i.fa.fa-2x.pull-right
                {:class    (if display "fa-toggle-on" "fa-toggle-off")
                 :title    (str (if display "Hide" "Show") " this person on the graph")
                 :on-click #(swap! !data people/toggle-display-person pkey)}]

               [:p.info name (age dob)]
               [:p.info-label "Date of Birth: "]
               [:p.info dob]
               [:p.info-label "Address: "]
               [:p.info address]]])
           people)]))))

