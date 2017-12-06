(ns gov.stockport.sonar.visualise.ui.components.recent-searches
  (:require
    [gov.stockport.sonar.visualise.state :refer [!status refresh-status! !search-history !search-control-state]]
    [gov.stockport.sonar.visualise.util.date :as d]
    [gov.stockport.sonar.visualise.util.fmt-help :as f]
    [reagent.core :as r]
    [gov.stockport.sonar.visualise.ui.search.search-history :as sh]
  ))

(defn nugget [{:keys [query-type search-term]}]
  ^{:key (gensym)}
  [:div.col.col-md-3.search-event-item
   [:label (name query-type)
    [:input
     {:type "text"
      :value search-term
      :name (name query-type)
      :id (name query-type)
      :size "20"
      :read-only "true"
      }]]]
  )

(defn search-event[event]
  ^{:key (gensym)}
  [:div.col.col-md-12.search-event
   [:div.row
    [:div.col.col-md-8 {:style {:text-align "left" :float "left"}}
     [:div.row (map nugget event)]]
    [:div.col.col-md-4
     {:style {:float "right" :text-align "right"}}
     [:button.btn.btn-info {:on-click sh/stored-search-criteria } "Search"] ]]])

(defn search-history []
  (r/with-let [expanded? (r/atom false)]
              [:div.col-md-12
               (if (true? @expanded?)
                 (map search-event  @!search-history)
                 (map search-event (take 4 @!search-history)))
               (if (> (count @!search-history) 4)
                 [:div.toggle-data
                  (if (true? @expanded?)
                    [:p  "SHOW LESS SEARCHES" [:br]
                     [:i.fa.fa-arrow-circle-up
                      {:on-click #(swap! expanded? not)}
                      ]]
                    [:p  "SHOW MORE SEARCHES" [:br]
                     [:i.fa.fa-arrow-circle-down {:on-click #(swap! expanded? not)}]])])]))

