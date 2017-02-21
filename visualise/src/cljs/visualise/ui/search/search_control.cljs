(ns visualise.ui.search.search-control
  (:require [visualise.ui.search.named-field :as nf :refer [search-named-field]]
            [visualise.common.results.handler :refer [default-handler]]
            [visualise.common.query.base :as qb]
            [visualise.query.client :refer [search]]))

(defn perform-search [handler [target value]]
  (search (-> (qb/query)
              (qb/with-size 5)
              (qb/with-field target value))
          handler))

(defn search-control [!state handler]
  [:div
   [search-named-field !state]
   [:input.btn.btn-primary
    {:type     :submit
     :value    "Search"
     :on-click #(perform-search handler (nf/current-value !state))}]])