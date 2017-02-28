(ns visualise.ui.search.search-control
  (:require [visualise.common.ui.search-control-state :as state]
            [visualise.common.ui.search-control-query :as query]
            [visualise.query.client :refer [search]]))

(def target-value (fn [elem] (-> elem .-target .-value)))

(defn selected-field [!state control-id {:keys [get-selected-field set-selected-field]}]
  [:select.input-lg
   {:value     (get-selected-field)
    :on-change #(set-selected-field (target-value %))}
   (map
     (fn [{:keys [target description]}]
       ^{:key target}
       [:option {:value target} description])
     (state/available-fields !state control-id))])

(defn search-criteria [!state control-id]
  (map
    (fn [{:keys [get-placeholder get-query set-query on-remove] :as sc}]
      [:div.panel.criteria-box
       [:div.panel-body

        [:div.form-group
         [selected-field !state control-id sc]
         [:button.input-lg.btn.btn-default.remove-criteria.pull-right {:type :button :on-click on-remove} [:i.fa.fa-times]]]
        [:div.form-group
         [:input.input-lg.col-sm-12
          {:type        :text
           :value       (get-query)
           :placeholder (get-placeholder)
           :on-change   #(set-query (target-value %))}]
         ]]])
    (state/get-all-search-criteria !state control-id)))

(defn add-criteria-button [on-click]
  [:div.form-group
    [:button.btn.btn-block.add-criteria
     {:on-click on-click}
     [:div
      [:i.fa.fa-plus.fa-2x.pull-left]
      [:p "Add Criteria"]]]])

(defn search-button [on-click]
  [:div.form-group
   [:button.btn.btn-block.search
    {:on-click on-click}
    [:div
     [:i.fa.fa-search.fa-2x.pull-left]
     [:p "Search"]]]])

(defn search-control [!state query-handler]
  (let [control-id (gensym "search-control-")]
    (state/init-search-control !state control-id)
    (fn []
      `[:div.search-control
        ~@(search-criteria !state control-id)
        ~[add-criteria-button #(state/add-search-criteria !state control-id)]
        ~[search-button #(search (query/extract-query (state/get-all-search-criteria !state control-id)) query-handler)]])))