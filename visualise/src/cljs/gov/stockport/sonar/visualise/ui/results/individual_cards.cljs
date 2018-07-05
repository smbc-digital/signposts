(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  "Renders cards on left hand side"
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :refer [address-summary date-of-birth -label]]
            [reagent.core :as reagent]
            [clojure.string :as str]))

(defonce !current (atom nil))

(defn- update-current-selected [!data]
  (reset! !current (:selected-event @!data)))

(defn- event-newly-selected? [!data]
  (and (:selected-event @!data)
       (not= @!current (:selected-event @!data))))

(defn wrap-scroll [!data scroll-fn]
  (fn [& _]
    (if (event-newly-selected? !data)
      (scroll-fn))
    (update-current-selected !data)))

(defn scroll-to-selected []
  (if-let [selected (.get (js/jQuery "div.has-selected-event") 0)]
    (let [top-of-selected-event (.-offsetTop selected)
          top-of-fixed-height (-> (js/jQuery "div.fixed-height")
                                  (.get 0)
                                  (.-offsetTop))]
      (-> (js/jQuery "div.fixed-height")
          (.animate (clj->js {:scrollTop (- top-of-selected-event top-of-fixed-height)}))))))

(defn card [!data]
  (let [highlighting-allowed? (:highlighting-allowed? @!data)]
    (fn [[{:keys [name dob] :as pkey} {:keys [has-selected-event? color highlighted? locked? areas event-types]}]]
      ^{:key (gensym)}
      [:div.mb-2.sp-individual
       {
        :title   (str (if highlighted? "Unhighlight" "Highlight") " this person on the graph")
        :class (str (and color (cljs.core/name color))
                    (when (not highlighted?) " blur")
                    (when has-selected-event? " has-selected-event"))
        :style {:width "97%"}}
       [:div.row.no-gutters.align-items-center.upper
        [:div.column.col-2.left.px-2.pt-2
           {:on-click
            #(when (or highlighted? highlighting-allowed?)
                (swap! !data people/toggle-highlight-person pkey))}
         [:i.fa " "]]
        [:div.column.col-8.px-2.pt-2.text-truncate.white
         {:style
          {:font=size "1.1em"}
          :on-click
          #(when (or highlighted? highlighting-allowed?)
              (swap! !data people/toggle-highlight-person pkey))}
         (str name)]
        [:div.column.col-2.px-2.pt-2.white {:style {:font=size "1.1em"}}
         [:i.fa.fa-lock
          {:style  {
                    :color (if locked? "#00cc00" "#1C3645")
                    :font-size "1.1em"
                    :padding-left "0"
                    :padding-right "12px"}
           :on-click #(swap! !data people/toggle-lock-person pkey)}]]]
       [:div.row.no-gutters.lower
        [:div.column.col-2.left
         {:on-click
          #(when (or highlighted? highlighting-allowed?)
              (swap! !data people/toggle-highlight-person pkey))}]
        [:div.column.col-10.px-2.pb-2.white
         {:on-click
          #(when (or highlighted? highlighting-allowed?)
              (swap! !data people/toggle-highlight-person pkey))}
         [:div
           [:strong "Date of birth"] [:br ]
           (date-of-birth pkey)]
         (let [areas (str/join ", " (sort areas))]
           [:div
            [:strong "Address on record"] [:br]
            (if (str/blank? areas) "no locations" areas)])
         (let [event-type (str/join ", " (map #(-label %)(sort event-types)))]
           [:div
            [:strong "Data type"] [:br]
            (if (str/blank? event-type) "" event-type)])]]])))

(defn cards-render [!data]
  (fn []
    (when (not-empty (:people @!data))
      [:div.cards
       [:p  "You can select up to " [:strong "10 individuals"] " to highlight their events on the graph"]
        [:div.select=cards
        [:div.reset-cards {:on-click #(swap! !data people/reset-selection)}
         [:div.icon[:i.fa.fa-times.ml-2]]
         [:div.reset-cards-text " Reset selection"]]
          [:div.reset-cards {:on-click #(swap! !data people/toggle-sort-by)}
          [:div.icon[:i.fa.fa-arrows-v.ml-2]]
          [:div.reset-cards-text
          (if (people/sort-by-relevance @!data) [:span " Sort by" [:br] "A-Z"] [:span " Sort by relevance"])]]]
       [:div.fixed-height (map (card !data) (people/sort-as @!data))]])))

(defn cards [!data]
  (fn []
    (reagent/create-class {:reagent-render       (cards-render !data)
                           :component-did-mount  (wrap-scroll !data scroll-to-selected)
                           :component-did-update (wrap-scroll !data scroll-to-selected)})))