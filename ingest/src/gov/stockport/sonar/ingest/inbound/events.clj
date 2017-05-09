(ns gov.stockport.sonar.ingest.inbound.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.helper.dates :as dates]
            [gov.stockport.sonar.ingest.helper.postcode :as p]
            [gov.stockport.sonar.ingest.clock :as clock]))

(defn- fix-timestamp [{{ts :timestamp} :data :as event}]
  (if (not (dates/iso-date-string? ts))
    (assoc-in event [:data :timestamp] (dates/date->iso-date-string (dates/parse ts)))
  event))

(defn- fix-dob [{{dob :dob} :data :as event}]
  (if-let [dob-as-date (dates/parse dob)]
    (assoc-in event [:data :dob] (dates/date->ymd-date-string dob-as-date))
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

(defn with-postcode [event]
  (if (not (nil? (get-in event [:data :postcode])))
    event
    (assoc-in event [:data :postcode] (p/extract (get-in event [:data :address])))))

(defn with-ingestion-timestamp [event]
  (assoc-in event [:data :ingestion-timestamp] (dates/date->iso-date-string (clock/now))))

(defn enhance [event]
  (-> event
      (with-postcode)
      (with-ingestion-timestamp)))