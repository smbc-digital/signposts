(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

(def age (fn [dob]
           (let [years (t/in-years (t/->Interval (f/parse (f/formatter "YYYY-mm-dd") dob) (t/now)))]
             (str " (" years " yrs)"))))



(defn cards [!data]
  (fn []
    (let [people (people/by-rank @!data)
          display-all? (:all-displayed? @!data)
          collapse-all? (:all-collapsed? @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event"
          (if (> (:total @!data) 1) "s") " from " (count people) " individual" (if (> (count people) 1) "s")]

         [:div.panel.panel-default.card-box
          [:div.panel-body

           [:i.fa.fa-2x.pull-left
            {:class    (if display-all? "fa-toggle-on" "fa-toggle-off")
             :title    (str (if display-all? "Hide" "Show") " all people on the graph")
             :on-click #(swap! !data people/toggle-display-all)}]
           [:p.info (if display-all? "Hide everyone" "Show everyone")]
           [:i.fa.fa-2x.pull-left
            {:class    (if collapse-all? "fa-arrow-down" "fa-arrow-up")
             :title    (str (if collapse-all? "Expand" "Collapse") " all cards")
             :on-click #(swap! !data people/toggle-collapse-all)}]
           [:p.info (if collapse-all? "Expand all cards"  "Collapse all cards")]]]

         [:div.fixed-height

          (map
            (fn [[{:keys [name dob address] :as pkey} {:keys [color display collapsed?]}]]
              ^{:key (gensym)}
              [:div.panel.panel-default.card-box
               {:class (str (cljs.core/name color)
                            (if display " focus" " blur"))}
               [:div.panel-heading.card-name
                {:on-click #(swap! !data update-in [:people pkey :collapsed?] not)}]
               [:div.panel-body

                [:i.fa.fa-2x.pull-right
                 {:class    (if display "fa-toggle-on" "fa-toggle-off")
                  :title    (str (if display "Hide" "Show") " this person on the graph")
                  :on-click #(swap! !data people/toggle-display-person pkey)}]


                [:p.info [:i.fa {:class    (if collapsed? "fa-arrow-down" "fa-arrow-up")
                                 :on-click #(swap! !data update-in [:people pkey :collapsed?] not)}] " " name (age dob)]

                (if (not collapsed?)
                  [:div
                   [:p.info-label "Date of Birth: "]
                   [:p.info dob]
                   [:p.info-label "Address: "]
                   [:p.info address]])
                ]])
            people)]]))))

