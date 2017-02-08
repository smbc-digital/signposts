(ns visualise.ui.records
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]))

(defn ts [ts]
  (f/unparse (:date f/formatters) ts))

(defn argh []
  [:tr [:td "1"]])

(defn record-list [!events]
  (fn []
    (let [results (:result @!events)]
      (if results
        [:table.results
           [:tr
            [:th "source"]
            [:th "type"]
            [:th "timestamp"]
            [:th "name"]
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
                [:td dob]
                [:td address]
                [:td (vals other)]]))
             results)]
        )
      )))