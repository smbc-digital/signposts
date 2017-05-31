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
  [:div.nugget {:on-click (remove-search-criteria query-callback !local item)}
   [:span.query (name query)]
   [:span.term val [:i.fa.fa-times]]])

(defn search-criteria-control [query-callback]
  (let [!local (r/atom {:query (:target (first qcs/options))
                        :terms []})]
    (fn []
      [:div.search-control.panel-body
       [:p.info {:style {:color "#ffffff"}} "Look for people"]
       [:div.nuggets
        [:select.input-sm
         {:value     (:query @!local)
          :autoFocus "autofocus"
          :on-change #(swap! !local assoc :query (keyword (-> % .-target .-value)))}
         (map
           (fn [{:keys [target description]}]
             ^{:key target}
             [:option {:value target} description])
           qcs/options)]
        [:input.input-sm {:value       (:val @!local)
                          :placeholder (get-in qcs/query-types [(:query @!local) :placeholder])
                          :on-change   #(swap! !local assoc :val (-> % .-target .-value))
                          :on-key-up   #(when (= 13 (-> % .-keyCode)) (add-search-criteria !local query-callback))}]]
       `[:div.nuggets
         ~@(map (partial nugget !local query-callback) (:terms @!local))]])))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))