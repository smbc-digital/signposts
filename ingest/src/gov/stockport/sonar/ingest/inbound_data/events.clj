(ns gov.stockport.sonar.ingest.inbound-data.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]
            [clojure.string :as str]))

(def valid-event? (partial s/valid? ::es/event))
(def invalid-event? (complement valid-event?))

(defn headers [row]
  (map #(keyword (str 'gov.stockport.sonar.spec.event-spec) (str/trim %)) row))

(defn csv->events [{:keys [csv-data] :as feed}]
  (let [headers (headers (first csv-data))
        supplied-events (map #(zipmap headers (map str/trim %)) (rest csv-data))]
    (assoc feed
      :valid-events (filter valid-event? supplied-events)
      :rejected-events (filter invalid-event? supplied-events))))

