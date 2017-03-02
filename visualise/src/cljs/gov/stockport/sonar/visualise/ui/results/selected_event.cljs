(ns gov.stockport.sonar.visualise.ui.results.selected-event
  (:require [clojure.string :as str]))


(def standard-keys [:name :dob :address :postcode])

(defn row [event ekey]
  [:tr [:th (str/capitalize (name ekey))] [:td (get event ekey)]])

(defn rows [event]
  (map
    (fn [ekey]
      [row event ekey])
    standard-keys))

(defn selected-event [!data]
  (fn []
    (let [selected (:selected-event @!data)]
      (when (not-empty selected)
        [:div.selected-event
         [:div.panel.panel-default
          [:div.panel-heading "SELECTED EVENT"]
          [:div.panel-body
           [:table.table-striped.table-condensed.results
            `[:tbody
              ~@(rows selected)]]]]]))))