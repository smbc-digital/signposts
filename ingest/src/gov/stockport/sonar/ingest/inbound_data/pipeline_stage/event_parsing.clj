(ns gov.stockport.sonar.ingest.inbound-data.pipeline-stage.event-parsing
  (:require [gov.stockport.sonar.ingest.util.dates :as dates]
            [gov.stockport.sonar.spec.event-spec :as es]
            [clojure.spec :as s]
            [clojure.string :as str]))

(def valid-event? (partial s/valid? ::es/event))
(def invalid-event? (complement valid-event?))

(defn headers [row]
  (println row)
  (map #(keyword (str 'gov.stockport.sonar.spec.event-spec) (str/trim %)) (filter not-empty row)))

(defn ->events [{:keys [csv-data] :as feed}]
  (let [headers (headers (first csv-data))
        supplied-events (map #(zipmap headers (map str/trim %)) (rest csv-data))]
    (assoc feed
      :valid-events (filter valid-event? supplied-events)
      :rejected-events (filter invalid-event? supplied-events))))

(defn ->canonical-events [{:keys [valid-events] :as feed}]
  (let [is-dmy? (dates/dmy-date-string? (::es/timestamp (first valid-events)))]
    (assoc feed
      :valid-events
      (map
        (fn [{:keys [::es/timestamp] :as event}]
          (if is-dmy?
            (assoc event ::es/timestamp (dates/date->iso-date-string (dates/dmy-date-string->date timestamp)))
            event))
        valid-events))))