(ns visualise.ui.records
  (:require
            [cljs-time.format :as f]
            [visualise.util.date :as d]
            [clojure.string :as str]))

(defn ts [ts]
  (f/unparse (:date f/formatters) ts))

(def surname #(last (str/split (:name %) #" ")))

(defn record-list [!state]
  (fn []
    (let [results (:result @!state)]
      (if (not-empty results)
        [:table.results
         [:tbody
          [:tr
           [:th "source"]
           [:th "type"]
           [:th "timestamp"]
           [:th "name"]
           [:th "age"]
           [:th "dob"]
           [:th "address"]
           [:th "other"]]
          (map
            (fn [event]
              (let [{:keys [event-source event-type timestamp name dob address]} event
                    other (dissoc event :event-source :event-type :timestamp :name :dob :address)]
                ^{:key (gensym)}
                [:tr
                 [:td event-source]
                 [:td event-type]
                 [:td (ts timestamp)]
                 [:td name]
                 [:td (d/age dob)]
                 [:td dob]
                 [:td address]
                 [:td (str/join ", " (vals other))]]))
            (sort-by (juxt surname :dob) results))]]))))
