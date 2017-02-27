(ns visualise.ui.search.search-control
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
      [:div.panel.criteria-box
       [:div.panel-body
        [:div.btn-group
         [selected-field !state control-id sc]
         [:button.btn.btn-default.pull-right {:type :button :on-click on-remove} [:i.fa.fa-times]]
         [:input.col-sm-12
          {:type        :text
           :value       (get-query)
           :placeholder (get-placeholder)
           :on-change   #(set-query (target-value %))}]
         ]]])
    (state/get-all-search-criteria !state control-id)))

(defn search-control [!state query-handler]
  (let [control-id (gensym "search-control-")]
    (state/init-search-control !state control-id)
    (fn []
      `[:div
        ~@(search-criteria !state control-id)
        ~[:div.form-group.col-sm-12
          [:button.btn.col-sm-12.add-criteria
           {:on-click #(state/add-search-criteria !state control-id)}
           [:i.fa.fa-plus.pull-left] "Add search criteria"]]
        ~[:div.form-group.col-sm-12
          [:button.btn.btn-primary.col-sm-12.search
           {:on-click #(search (query/extract-query (state/get-all-search-criteria !state control-id)) query-handler)}
           [:i.fa.fa-search-plus.pull-left] "Search"]]])))