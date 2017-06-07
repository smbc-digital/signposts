(ns gov.stockport.sonar.visualise.ui.search.new-search-control
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]))

(defn add-search-criteria [!local query-callback]
  (when (:val @!local)
    (query-callback (:terms (swap! !local
                                   (fn [local]
                                     (-> local
                                         (update :terms concat [(assoc (select-keys local [:query :val])
                                                                  :idx (count (:terms local)))])
                                         (dissoc :val))))))))

(defn remove-search-criteria [query-callback !local item]
  (fn []
    (let [terms (:terms (swap! !local update :terms (fn [terms] (filter (fn [term] (not (= item term))) terms))))]
      (query-callback terms))))

(defn nugget [!local query-callback {:keys [query val] :as item}]
  [:div.input-group.nugget.mr-2
   [:span.input-group-addon.name (name query)]
   [:span.input-group-addon.val val
    [:i.fa.fa-times.ml-2
     {:on-click (remove-search-criteria query-callback !local item)}]]])

(defonce initial-state (r/atom {:query (:target (first qcs/options))
                                :terms []}))

(defn search-criteria-control [query-callback]
  (let [!local initial-state]
    (fn []
      [:div.container-fluid.py-1
       {:style {:background-color "#1d2932"}}
       [:div.form-inline
        [:select.custom-select.form-control.mr-2
         {:value     (:query @!local)
          :autoFocus "autofocus"
          :on-change #(swap! !local assoc :query (keyword (-> % .-target .-value)))}
         (map
           (fn [{:keys [target description]}]
             ^{:key target}
             [:option {:value target} description])
           qcs/options)]
        [:div.input-group
         [:input.form-control {:value       (:val @!local)
                               :placeholder (get-in qcs/query-types [(:query @!local) :placeholder])
                               :on-change   #(swap! !local assoc :val (-> % .-target .-value))
                               :on-key-up   #(when (= 13 (-> % .-keyCode)) (add-search-criteria !local query-callback))}]
         [:span.input-group-btn
          [:button.btn.btn-success.mr-2
           {:on-click #(add-search-criteria !local query-callback)}
           [:i.fa.fa-search]]]]
        `[:span.py-1
          {:style {:display   :inline-flex
                   :flex-wrap :wrap}}
          ~@(map (partial nugget !local query-callback) (:terms @!local))]]])))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))

