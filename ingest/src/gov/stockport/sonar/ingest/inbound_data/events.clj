(ns gov.stockport.sonar.ingest.inbound-data.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]))

(def valid-event? (partial s/valid? ::es/event))
(def invalid-event? (complement valid-event?))

(defn csv->events [{:keys [csv-data] :as feed}]
  (let [headers (map #(keyword (str 'gov.stockport.sonar.spec.event-spec) %) (first csv-data))
        supplied-events (map #(zipmap headers %) (rest csv-data))]
    (assoc feed
      :valid-event (filter valid-event? supplied-events)
      :rejected-events (filter invalid-event? supplied-events))))

