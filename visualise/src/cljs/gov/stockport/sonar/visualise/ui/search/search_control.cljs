(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [gov.stockport.sonar.visualise.common.ui.search-control-state :as state]
            [gov.stockport.sonar.visualise.query.client :refer [search]]))

(def target-value (fn [elem] (-> elem .-target .-value)))

(defn selected-field [!state control-id {:keys [get-selected-field set-selected-field]}]
  [:select.input-lg.col-sm-9
   {:value     (get-selected-field)
    :on-change #(set-selected-field (target-value %))}
   (map
     (fn [{:keys [target description]}]
       ^{:key target}
       [:option {:value target} description])
     (state/available-fields !state control-id))])

(defn search-criteria [!state control-id perform-search]
  (map
    (fn [{:keys [get-placeholder get-query set-query on-remove] :as sc}]
      [:div.panel.criteria-box
       [:div.panel-body.col-sm-12

        [:div.form-group.row
         [selected-field !state control-id sc]
         [:button.input-lg.btn.btn-default.remove-criteria.pull-right {:type :button :on-click on-remove} [:i.fa.fa-times]]]
        [:div.form-group.row
         [:input.input-lg.col-sm-12
          {:type        :text
           :value       (get-query)
           :autoFocus   "autofocus"
           :placeholder (get-placeholder)
           :on-change   #(set-query (target-value %))
           :on-key-up   #(when (= 13 (-> % .-keyCode)) (perform-search))}]
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

(defn search-control [!app query-handler]
  (let [control-id (gensym "search-control-")
        search-fn (fn [] (search
                           (state/extract-query-defs (state/get-all-search-criteria !app control-id))
                           query-handler))]
    (state/init-search-control !app control-id)
    (fn []
      `[:div.search-control.panel-body

        ~@(search-criteria !app control-id search-fn)
        ~[add-criteria-button #(state/add-search-criteria !app control-id)]
        ~[search-button search-fn]])))