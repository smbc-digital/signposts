(ns visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [visualise.ui.search.named-field :as nf :refer [search-named-field]]
            [visualise.common.results.handler :refer [default-handler]]
            [visualise.common.query.base :as qb]
            [visualise.query.client :refer [search]]))

(defn perform-search [handler [target value]]
  (search (-> (qb/query)
              (qb/with-size 5)
              (qb/with-match target value))
          handler))

(defn search-control [!state handler]
  (let []
    (fn []
      [:div
       [:div.form-group
        [search-named-field !state]
        [:button [:i.fa.fa-plus]]]
       [:input.btn.btn-primary
        {:type     :submit
         :value    "Search"
         :on-click #(perform-search handler (nf/current-value !state))}]])))