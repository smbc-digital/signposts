(ns visualise.ui.results.raw-table
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [visualise.util.date :as d]
            [clojure.string :as str]))

(defn ts [ts]
  (f/unparse (:date f/formatters) ts))

(def surname #(last (str/split (:name %) #" ")))

(defn sortable-header [!sort description sort-key]
  [:th description " " [:i.fa.fa-sort-asc {:on-click #(reset! !sort sort-key)}]])

(defn raw-table [!data]
  (let [!sort-fn (r/atom (juxt surname :dob))]
    (fn []
      (let [results (:result @!data)]
        (if (not-empty results)
          [:div.panel.panel-default.criteria-box
           [:div.panel-heading "Raw Data"]
           [:div.panel-body
            [:table.table-striped.table-condensed.results
             [:thead
              [:tr
               [sortable-header !sort-fn "source" :event-source]
               [sortable-header !sort-fn "type" :event-type]
               [sortable-header !sort-fn "timestamp" #(d/as-millis (:timestamp %))]
               [sortable-header !sort-fn "name" surname]
               [sortable-header !sort-fn "age" :dob]
               [sortable-header !sort-fn "dob" :dob]
               [sortable-header !sort-fn "address" :address]]]
             [:tbody
              (map
                (fn [event]
                  (let [{:keys [event-source event-type timestamp name dob address]} event]
                    ^{:key (gensym)}
                    [:tr
                     [:td event-source]
                     [:td event-type]
                     [:td (ts timestamp)]
                     [:td name]
                     [:td (d/age dob)]
                     [:td dob]
                     [:td address]]))
                (sort-by @!sort-fn results))]]]])))))
