(ns gov.stockport.sonar.visualise.ui.results.selected-event
  (:require [clojure.string :as str]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]))

(def standard-keys [:name :dob :address :postcode :timestamp :score])

(def custom-formatter (f/formatter "dd MMM yyyy HH:mm:ss"))

(defn format-label [label]
  (cond (= label "dob") "DOB"
        (= label "event-source") "Event Source"
        (= label "event-type") "Event Type"
        (= label "ingestion-timestamp") "Ingestion Timestamp"
      :else (str/capitalize label
    )
  ))

(defn unparse-timestamp [event]
  (if-let [ts (:timestamp event)]
    (assoc event :timestamp (f/unparse custom-formatter ts))
    event))

(def dob-unformatter (f/formatter "yyyy-mm-dd"))
(def dob-formatter (f/formatter "dd MMM yyyy"))

(defn unparse-dob [event]
  (if-let [ts (:dob event)]
    (assoc event :dob (f/unparse dob-formatter (f/parse dob-unformatter ts)))
    event))

(defn selected-kvs [event]
  (let [event-with-formatted-timestamp (-> event unparse-timestamp unparse-dob)
        other-keys (sort (keys (apply dissoc (dissoc event :id) standard-keys)))]
    (map
      (fn [k] [k (get event-with-formatted-timestamp k "")])
      (concat standard-keys other-keys))))

(defn row [[k v]]
  [:tr [:th (format-label (name k))] [:td v]])

(defn rows [event]
  (map row (selected-kvs event)))

(defn signpost-fields [event]
  (let [signpost (s/signpost-for event)]
    (map
      (fn [{:keys [name value]}]
        [:tr [:th name] [:td value]])
      (:fields signpost))))

(defn selected-event [!data]
  (fn []
    (let [selected (:selected-event @!data)]
      (when (not-empty selected)
        [:div.selected-event
         [:div.panel-group
          [:div.panel.panel-default.event-details.col-sm-5
           [:div.panel-heading (:event-type selected)]
           [:div.panel-body
            [:table.table-striped.table-condensed.results.selected-results
             `[:tbody
               ~@(rows selected)]
             ]]]
          [:div.panel.panel-default.contact-panel.col-sm-7
           [:div.panel-heading.contact-heading "SIGNPOST"]
           [:div.panel-body
            [:table.table-condensed.results.selected-results
            `[:tbody
              ~@(signpost-fields selected)]]]]]]))))