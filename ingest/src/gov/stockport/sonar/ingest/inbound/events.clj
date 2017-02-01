(ns gov.stockport.sonar.ingest.inbound.events
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.spec.event-spec :as es]))

(defn validate [{:keys [data error] :as event}]
  (let [event-data (es/promote-to-namespaced-keywords data)]
    (if (or error (s/valid? ::es/event event-data))
      event
      (merge event {:error :event-spec-validation}))))

