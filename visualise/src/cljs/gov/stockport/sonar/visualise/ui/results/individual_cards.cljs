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
  (if displayed? "fa-check-square" "fa-square"))

(defn card [!data]
  (let [highlighting-allowed? (:highlighting-allowed? @!data)]
    (fn [[{:keys [name dob] :as pkey} {:keys [has-selected-event? color highlighted? collapsed?]}]]
      ^{:key (gensym)}
      [:div.mb-2.sp-individual
       {:class (str (and color (cljs.core/name color))
                    (when (not highlighted?) " blur")
                    (when has-selected-event? " has-selected-event"))}
       [:div.row.no-gutters.align-items-center.upper
        [:div.column.col-1.pt-2
         [:center
          (if (or highlighted? highlighting-allowed?)
            [:i.fa
             {:style    {:color :white}
              :class    (displayed-icon highlighted?)
              :title    (str (if highlighted? "Unhighlight" "Highlight") " this person on the graph")
              :on-click #(swap! !data people/toggle-highlight-person pkey)}]
            [:i.fa.fa-square {:style {:filter "opacity(0.1)"}}])]]
        [:div.column.col-11.px-2.pt-2.text-truncate
         (str name (age dob))]]

       [:div.row.no-gutters.lower
        [:div.column.col-1]
        [:div.column.col-11.px-2.pb-2
         [:div
          [:i.fa.fa-calendar] " " (date-of-birth pkey)]
         [:div
          [:i.fa.fa-home] " " (or (address-summary pkey) "no address")]]]])))

(defn cards-render [!data]
  (fn []
    (let [people (people/by-rank @!data)
          collapse-all? (:all-collapsed? @!data)]
      (when (not-empty people)
        [:div.cards
         [:p "Select up to " [:b "6 individuals"] " to highlight their events on the graph"]

         ;[:p
         ;  [:i.fa.fa-exchange.fa-rotate-90.pull-left
         ;   {:title    (str (if collapse-all? "Expand" "Collapse") " all cards")
         ;    :on-click #(swap! !data people/toggle-collapse-all)}]
         ;   (if collapse-all? "expand all cards" "collapse all cards")]

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


