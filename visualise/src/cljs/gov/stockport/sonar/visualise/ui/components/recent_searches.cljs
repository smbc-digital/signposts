(ns gov.stockport.sonar.visualise.ui.components.recent-searches
  "Library for dealing with recent searches uses search history which use local storage"
  (:require
    [gov.stockport.sonar.visualise.state :refer [!search-history]]
    [reagent.core :as r]
    [gov.stockport.sonar.visualise.ui.search.search-history :refer [stored-search-criteria]]))

(defn nugget [{:keys [query-type search-term]}]
  ^{:key (gensym)}
  [:div.col.col-md-3.search-event-item
   [:label query-type
    [:input
     {:type "text"
      :value search-term
      :id query-type
      :size "20"
      :read-only "true"}]]])

(defn search-event[idx event]
  ^{:key (gensym)}
  [:div.col.col-md-12.search-event
   [:div.row
    [:div.col.col-md-8 {:style {:text-align "left" :float "left"}}
     [:div.row (map nugget event)]]
    [:div.col.col-md-4.search-history-button
     [:button.btn.search {:on-click #(stored-search-criteria idx)} "Search"]]]])

(defn search-history []
  ^{:key (gensym)}
  (r/with-let [expanded? (r/atom false)]
              [:div.col-md-12
               (if (true? @expanded?)
                 (map-indexed search-event  @!search-history)
                 (map-indexed search-event (take 4 @!search-history)))
               (if (> (count @!search-history) 4)
                 [:div.toggle-data
                  (if (true? @expanded?)
                    [:p  "SHOW LESS SEARCHES" [:br]
                     [:i.fa.fa-arrow-circle-up
                      {:on-click #(swap! expanded? not)}
                      ]]
                    [:p  "SHOW MORE SEARCHES" [:br]
                     [:i.fa.fa-arrow-circle-down {:on-click #(swap! expanded? not)}]])])]))

