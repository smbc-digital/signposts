(ns gov.stockport.sonar.visualise.ui.results.individual-cards
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.data.people :as people]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :refer [address-summary date-of-birth]]
            [reagent.core :as reagent]
            [clojure.string :as str]
            ))

(def age
  (fn [dob]
    (when dob
      (let [years (t/in-years (t/->Interval (f/parse (f/formatter "YYYY-mm-dd") dob) (t/now)))]
        (str " (" years " yrs)")))))



(defn displayed-icon [displayed?]
  (if displayed? "fa-check-square" "fa-square"))


(defn card [!data]
  (let [highlighting-allowed? (:highlighting-allowed? @!data)]
    (fn [[{:keys [name dob] :as pkey} {:keys [has-selected-event? color highlighted? locked? areas]}]]
      ^{:key (gensym)}
      [:div.mb-2.sp-individual
       {
        :title   (str (if highlighted? "Unhighlight" "Highlight") " this person on the graph")
        :class (str (and color (cljs.core/name color))
                    (when (not highlighted?) " blur")
                    (when has-selected-event? " has-selected-event"))}
       [:div.row.no-gutters.align-items-center.upper
        [:div.column.col-2.left.px-2.pt-2 {:on-click #(swap! !data people/toggle-highlight-person pkey) }   [:i.fa " "]]
        [:div.column.col-8.px-2.pt-2.text-truncate {:style {:font=size "1.1em"}:on-click #(swap! !data people/toggle-highlight-person pkey)}
         (str name)]
        [:div.column.col-2.px-2.pt-2
         [:i.fa
          {:style  {
                    :color (if locked? "#00cc00" "#1C3645")
                    :font-size "1.1em"
                    :padding-left "10px"
                    :padding-right "12px"}
           :class    "fa-lock"
           :on-click #(swap! !data people/toggle-lock-person pkey)}]]]

       [:div.row.no-gutters.lower
        [:div.column.col-2.left {:on-click #(swap! !data people/toggle-highlight-person pkey)}]
        [:div.column.col-10.px-2.pb-2{:on-click #(swap! !data people/toggle-highlight-person pkey)}
         [:div
           [:strong "Date of birth"] [:br ]
           (date-of-birth pkey)]
         (let [areas (str/join ", " (sort areas))]
           [:div
            [:strong "Address on record"] [:br ]
            (if (empty? areas) "no locations" areas)])]]])))

(defn cards-render [!data]
  (fn []
    (when (not-empty (:people @!data))
      [:div.cards
       [:p "You can select up to " [:b "6 individuals"] " to highlight their events on the graph"]
        [:div.select=cards {:style {:width "100%" :padding "5px 2px 2px 3px"}}
        [:span.reset-cards
        [:i.fa.fa-times.ml-2 {:on-click #(swap! !data people/reset-selection)}
         [:span {:style {:font-family [:arial :sans-serif] :font-weight "530"}} " Reset selection"
          ]]]

       [:span.reset-cards
        [:i.fa.fa-arrows-v.ml-2
         {:id "sort-cards" :on-click #(swap! !data people/toggle-sort-by)}
         [:span {:style {:font-family [:arial :sans-serif] :font-weight "530"}}
          (if (people/sort-by-relevance @!data) " Sort by A - Z" " Sort by relevance")
          ]]]]
       [:div.fixed-height (map (card !data) (people/sort-as @!data))]])))

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


(defn cards [!data]
  (fn []
    (reagent/create-class {:reagent-render       (cards-render !data)
                           :component-did-mount  (wrap-scroll !data scroll-to-selected)
                           :component-did-update (wrap-scroll !data scroll-to-selected)
                           })))

