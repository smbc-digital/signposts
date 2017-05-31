(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :refer [address-summary date-of-birth]]
            [reagent.core :as reagent]))

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

(defn card [!data]
  (let [highlighting-allowed? (:highlighting-allowed? @!data)]
    (fn [[{:keys [name dob] :as pkey} {:keys [has-selected-event? color highlighted? collapsed? locked?]}]]
      ^{:key (gensym)}
      [:div.panel.panel-default.card-box
       {:class (str (and color (cljs.core/name color))
                    (when (not highlighted?) " blur")
                    (when has-selected-event? " has-selected-event"))}
       [:div.panel-heading.card-name]
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
           [:div [:span "date-of-birth: "] [:span (date-of-birth pkey)]]
           [:div [:span "address: "] [:span (address-summary pkey)]]])]])))

(defn cards-render [!data]
  (fn []
    (let [people (people/by-rank @!data)
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

         [:div.fixed-height (map (card !data) people)]]))))

(defn scroll-to-selected [& _]
  (if-let [selected (.get (js/jQuery "div.has-selected-event") 0)]
    (let [top-of-selected-event (.-offsetTop selected)
          top-of-fixed-height (-> (js/jQuery "div.fixed-height")
                                  (.get 0)
                                  (.-offsetTop))]
      (-> (js/jQuery "div.fixed-height")
          (.animate (clj->js {:scrollTop (- top-of-selected-event top-of-fixed-height)}))))))

(defn cards [!data]
  (fn []
    (reagent/create-class {:reagent-render       (cards-render !data)
                           :component-did-mount  scroll-to-selected
                           :component-did-update scroll-to-selected})))


