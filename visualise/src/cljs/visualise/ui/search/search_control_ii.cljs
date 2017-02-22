(ns visualise.ui.search.search-control-ii
  (:require [visualise.common.ui.search-control-state :as state]
            [visualise.common.ui.search-control-query :as query]
            [visualise.query.client :refer [search]]))

(def target-value (fn [elem] (-> elem .-target .-value)))

(defn selected-field [!state control-id {:keys [get-selected-field set-selected-field]}]
  [:select {:value     (get-selected-field)
            :on-change #(set-selected-field (target-value %))}
   (map
     (fn [{:keys [target description]}]
       ^{:key target}
       [:option {:value target} description])
     (state/available-fields !state control-id))])

(defn search-criteria [!state control-id]
  (map
    (fn [{:keys [get-placeholder get-query set-query on-remove] :as sc}]
      [:div.panel.panel-default.criteria-box
       [:div.panel-heading "Search criteria"
        [:button.btn.btn-default.pull-right {:type :button :on-click on-remove} "Remove"]]
       [:div.panel-body
        [:div.btn-group
         [selected-field !state control-id sc]
         [:input.col-sm-12
          {:type        :text
           :value       (get-query)
           :placeholder (get-placeholder)
           :on-change   #(set-query (target-value %))}]
         ]]])
    (state/get-all-search-criteria !state control-id)))

(defn search-control-ii [!state query-handler]
  (let [control-id (gensym "search-control-")]
    (state/init-search-control !state control-id)
    (fn []
      `[:div
        ~@(search-criteria !state control-id)
        ~[:div.form-group.col-sm-12
          [:button.btn.btn-primary.col-sm-12 {:on-click #(state/add-search-criteria !state control-id)} "Add search criteria"]]
        ~[:div.form-group.col-sm-12
          [:button.btn.btn-primary.col-sm-12 {:on-click #(search (query/extract-query (state/get-all-search-criteria !state control-id)) query-handler)} "Search"]]])))