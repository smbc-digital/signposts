(ns gov.stockport.sonar.ingest.inbound.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]))

(defn promote-to-namespaced-keywords [event]
  (reduce merge {} (map (fn [[k v]] {(keyword (str 'gov.stockport.sonar.spec.event-spec) (name k)) v}) event)))

(defn validate [{:keys [data] :as event}]
  (let [event-data (promote-to-namespaced-keywords data)]
    (if (s/valid? ::es/event event-data)
      event
      (merge event {:error :event-spec-validation}))))
