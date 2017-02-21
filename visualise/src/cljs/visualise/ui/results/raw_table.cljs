(ns visualise.ui.results.raw-table
  (:require [cljs-time.format :as f]
            [visualise.util.date :as d]
            [clojure.string :as str]))

(defn ts [ts]
  (f/unparse (:date f/formatters) (f/parse (:date-time f/formatters) ts)))

(def surname #(last (str/split (:name %) #" ")))

(defn raw-table [!data]
  (fn []
    (let [results (:result @!data)]
      (if (not-empty results)
        [:div.panel.panel-default.criteria-box
         [:div.panel-heading "Raw Data"]
         [:div.panel-body
          [:table.table-striped.table-condensed.results
           [:thead
            [:tr
             [:th "source"]
             [:th "type"]
             [:th "timestamp"]
             [:th "name"]
             [:th "age"]
             [:th "dob"]
             [:th "address"]]]
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
              (sort-by (juxt surname :dob) results))]]]]))))
