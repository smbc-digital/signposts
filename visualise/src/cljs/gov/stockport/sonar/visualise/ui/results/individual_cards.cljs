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
  (if displayed? "fa-check-square" "fa-square-o"))

(defn collapsed-icon [collapsed?]
  (if collapsed? "fa-arrow-down" "fa-arrow-up"))

(defn locked-icon [locked?]
  (if locked? "fa-lock" "fa-unlock"))

(defn cards [!data]
  (fn []
    (let [people (people/by-rank @!data)
          highlighting-allowed? (:highlighting-allowed? @!data)
          collapse-all? (:all-collapsed? @!data)]
      (when (not-empty people)
        [:div.cards
         [:p.results-confirmation (people/results-summary @!data)]
         [:p "You can select up to 6 individuals to highlight"]

         [:div.panel.panel-default.card-box
          [:div.panel-body
           [:i.fa.fa-2x.pull-left
            {:class    (if collapse-all? "fa-arrow-down" "fa-arrow-up")
             :title    (str (if collapse-all? "Expand" "Collapse") " all cards")
             :on-click #(swap! !data people/toggle-collapse-all)}]
           [:p.info (if collapse-all? "Expand all cards" "Collapse all cards")]]]

         [:div.fixed-height
          (map
            (fn [[{:keys [name dob address] :as pkey} {:keys [color highlighted? collapsed? locked?]}]]
              ^{:key (gensym)}
              [:div.panel.panel-default.card-box
               {:class (str (and color (cljs.core/name color))
                            (when (not highlighted?) " blur"))}
               [:div.panel-heading.card-name
                {:on-click #(swap! !data update-in [:people pkey :collapsed?] not)}]
               [:div.panel-body

                (if (or highlighted? highlighting-allowed?)
                  [:i.fa.fa-2x.pull-right
                   {:class    (displayed-icon highlighted?)
                    :title    (str (if highlighted? "Unhighlight" "Highlight") " this person on the graph")
                    :on-click #(swap! !data people/toggle-highlight-person pkey)}])

                ;[:i.fa.fa-2x.pull-right
                ; {:class    (locked-icon locked?)
                ;  :title    (str (if locked? "Hide" "Show") " this person on the graph")
                ;  :on-click #(swap! !data update-in [:people pkey :locked?] not)}]

                [:p.info name (age dob)]

                (if (not collapsed?)
                  [:div
                   [:p.info-label "Date of Birth: "]
                   [:p.info dob]
                   [:p.info-label "Address: "]
                   [:p.info address]])]])

            people)]]))))

