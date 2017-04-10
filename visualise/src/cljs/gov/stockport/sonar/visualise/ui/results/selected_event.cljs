(ns gov.stockport.sonar.visualise.ui.results.selected-event
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [cljs-time.format :as f]))

(def standard-keys [:name :dob :address :postcode :timestamp :score])

(def custom-formatter (f/formatter "dd-MM-yyyy HH:mm:ss"))

(defn unparse-timestamp [event]
  (if-let [ts (:timestamp event)]
    (assoc event :timestamp (f/unparse custom-formatter ts))
    event))

(defn selected-kvs [event]
  (let [event-with-formatted-timestamp (unparse-timestamp event)
        other-keys (sort (keys (apply dissoc (dissoc event :id) standard-keys)))]
    (map
      (fn [k] [k (get event-with-formatted-timestamp k "")])
      (concat standard-keys other-keys))))

(defn row [[k v]]
  [:tr [:th (str/capitalize (name k))] [:td v]])

(defn rows [event]
  (map row (selected-kvs event)))

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