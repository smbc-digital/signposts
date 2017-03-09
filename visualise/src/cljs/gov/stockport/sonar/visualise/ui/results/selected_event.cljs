(ns gov.stockport.sonar.visualise.ui.results.selected-event
  (:require [clojure.string :as str]
            [reagent.core :as r]))

(def standard-keys [:name :dob :address :postcode])

(defn selected-kvs [event]
  (let [other-keys (sort (keys (apply dissoc (dissoc event :timestamp) standard-keys)))]
    (map
      (fn [k] [k (get event k "")])
        (concat standard-keys other-keys))))

(defn row [event ekey]
  [:tr [:th (str/capitalize (name ekey))] [:td (get event ekey)]])

(defn rows [event]
  (map
    (fn [ekey]
      [row event ekey])
    selected-kvs))

(defn selected-event [!data]
  (fn []
    (let [selected (:selected-event @!data)]
      (when (not-empty selected)
        [:div.selected-event
         [:div.panel-group
          [:div.panel.panel-default.event-details
           [:div.panel-heading "SELECTED EVENT"]
           [:div.panel-body
            [:table.table-striped.table-condensed.results.selected-results
             `[:tbody
               ~@(rows selected)]
             ]]]
          [:div.panel.panel-default.contact-panel
           [:div.panel-heading.contact-heading "CONTACT"]
           [:div.panel-body
            [:p.contact-label "System"]
            [:p.contact-label "Name"]
            [:p.contact-label "Number"]
            [:p.contact-label "Email"]]]
          ]]))))