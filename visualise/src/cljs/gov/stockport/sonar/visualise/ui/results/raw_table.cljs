(ns gov.stockport.sonar.visualise.ui.results.raw-table
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.date :as d]
            [clojure.string :as str]))

(defn ts [ts]
  (f/unparse (:date f/formatters) ts))

(def surname #(last (str/split (:name %) #" ")))

(defn sortable-header [!sort description sort-func]
  [:th description " " [:i.fa.fa-sort
                        {:on-click #(swap! !sort (fn [s] (-> s
                                                             (assoc :sort-func sort-func)
                                                             (update :ascending not))))}]])

(defn sortit [!sort list]
  (let [sorted (sort-by (:sort-func @!sort) list)]
    (if (:ascending @!sort) sorted (reverse sorted))))

(defn raw-table [!data]
  (let [!sort-fn (r/atom {:sort-func :score
                          :ascending false})]
    (fn []
      (let [results (:result @!data)]
        (if (not-empty results)
            [:table.table-striped.table-condensed.results
             [:thead
              [:tr
               [sortable-header !sort-fn "score" :score]
               [sortable-header !sort-fn "source" (comp :event-source not)]
               [sortable-header !sort-fn "type" :event-type]
               [sortable-header !sort-fn "timestamp" #(d/as-millis (:timestamp %))]
               [sortable-header !sort-fn "name" surname]
               [sortable-header !sort-fn "age" :dob]
               [sortable-header !sort-fn "dob" :dob]
               [sortable-header !sort-fn "address" :address]]]
             [:tbody
              (map
                (fn [event]
                  (let [{:keys [score event-source event-type timestamp name dob address]} event]
                    ^{:key (gensym)}
                    [:tr
                     [:td score]
                     [:td event-source]
                     [:td event-type]
                     [:td (ts timestamp)]
                     [:td name]
                     [:td (d/age dob)]
                     [:td dob]
                     [:td address]]))
                (sortit !sort-fn results))]])))))
