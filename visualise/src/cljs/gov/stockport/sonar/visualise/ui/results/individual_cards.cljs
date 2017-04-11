(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

(def age
  (fn [dob]
    (when dob
      (let [years (t/in-years (t/->Interval (f/parse (f/formatter "YYYY-mm-dd") dob) (t/now)))]
        (str " (" years " yrs)")))))

(defn displayed-icon [displayed?]
  (if displayed? "fa-toggle-on" "fa-toggle-off"))

(defn collapsed-icon [collapsed?]
  (if collapsed? "fa-arrow-down" "fa-arrow-up"))

(defn locked-icon [locked?]
  (if locked? "fa-lock" "fa-unlock"))

(defn cards [!data]
  (fn []
    (let [people (people/by-rank @!data)
          all-displayed? (:all-displayed? @!data)
          all-collapsed? (:all-collapsed? @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation "Your search returned " (:total @!data) " event"
          (if (> (:total @!data) 1) "s") " from " (count people) " individual" (if (> (count people) 1) "s")]

         [:div.panel.panel-default.card-box
          [:div.panel-body

           [:i.fa.fa-2x.pull-left
            {:class    (displayed-icon all-displayed?)
             :title    (str (if all-displayed? "Hide" "Show") " all people on the graph")
             :on-click #(swap! !data people/toggle-display-all)}]
           [:p.info (if all-displayed? "Hide everyone" "Show everyone")]
           [:i.fa.fa-2x.pull-left
            {:class    (collapsed-icon all-collapsed?)
             :title    (str (if all-collapsed? "Expand" "Collapse") " all cards")
             :on-click #(swap! !data people/toggle-collapse-all)}]
           [:p.info (if all-collapsed? "Expand all cards" "Collapse all cards")]]]

         [:div.fixed-height

          (map
            (fn [[{:keys [name dob address] :as pkey} {:keys [color displayed? collapsed? locked?]}]]
              ^{:key (gensym)}
              [:div.panel.panel-default.card-box
               {:class (str (cljs.core/name color)
                            (when (not displayed?) " blur"))}
               [:div.panel-heading.card-name
                {:on-click #(swap! !data update-in [:people pkey :collapsed?] not)}]
               [:div.panel-body

                [:i.fa.fa-2x.pull-right
                 {:class    (displayed-icon displayed?)
                  :title    (str (if displayed? "Hide" "Show") " this person on the graph")
                  :on-click #(swap! !data people/toggle-display-person pkey)}]

                ;[:i.fa.fa-2x.pull-right
                ; {:class    (locked-icon locked?)
                ;  :title    (str (if locked? "Hide" "Show") " this person on the graph")
                ;  :on-click #(swap! !data update-in [:people pkey :locked?] not)}]

                [:p.info [:i.fa {:class    (collapsed-icon collapsed?)
                                 :on-click #(swap! !data update-in [:people pkey :collapsed?] not)}] " " name (age dob)]

                (if (not collapsed?)
                  [:div
                   [:p.info-label "Date of Birth: "]
                   [:p.info dob]
                   [:p.info-label "Address: "]
                   [:p.info address]])
                ]])
            people)]]))))

