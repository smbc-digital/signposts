(ns visualise.ui.search.search-control
  (:require [visualise.ui.search.named-field :as nf :refer [search-named-field]]
            [visualise.common.results.handler :refer [default-handler]]
            [visualise.common.query.base :as qb]
            [visualise.query.client :refer [search]]))

(defn perform-search [!state name]
  (search (-> (qb/query)
              (qb/with-size 5)
              (qb/with-field :name name))
          (fn [result] (swap! !state assoc :results result))))

(defn search-control
  ([!state] (search-control !state perform-search))
  ([!state query-fn]
   [:div
    [search-named-field !state]
    [:input {:type     :submit
             :value    "Search"
             :on-click #(query-fn (nf/current-value !state))}]]))