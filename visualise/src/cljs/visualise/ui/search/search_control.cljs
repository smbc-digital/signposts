(ns visualise.ui.search.search-control
  (:require [visualise.ui.search.named-field :as nf :refer [search-named-field]]))

(defn search-control
  ([!state] (search-control !state (fn [x] (println x))))
  ([!state query-fn]
   [:div
    [search-named-field !state]
    [:input {:type     :submit
             :value    "Search"
             :on-click #(query-fn (nf/current-value !state))}]]))