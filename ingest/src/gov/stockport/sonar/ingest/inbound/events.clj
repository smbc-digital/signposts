(ns gov.stockport.sonar.ingest.inbound.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.util.dates :as dates]))

(defn- fix-timestamp [{{ts :timestamp} :data :as event}]
  (if (dates/dmy-date-string? ts)
    (assoc-in event [:data :timestamp] (dates/date->iso-date-string (dates/dmy-date-string->date ts)))
    event))

(defn- fix-dob [{{dob :dob} :data :as event}]
  (if-let [dob-as-date (dates/parse dob)]
    (assoc-in event [:data :dob] (dates/date->dmy-date-string dob-as-date))
    event))

(defn validate [{:keys [data error] :as event}]
  (let [event-data (es/promote-to-namespaced-keywords data)]
    (if (or error (s/valid? ::es/event event-data))
      event
      (merge event {:error :event-spec-validation}))))

(defn normalise [event]
  (-> event
      (fix-timestamp)
      (fix-dob)))