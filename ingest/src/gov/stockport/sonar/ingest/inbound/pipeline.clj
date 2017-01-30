(ns gov.stockport.sonar.ingest.inbound.pipeline
  (:require [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.clock :as clock]
            [gov.stockport.sonar.ingest.inbound.pipeline-stage.event-parsing
             :refer [->events ->canonical-events]]
            [gov.stockport.sonar.ingest.client.elastic-search-client :refer [->elastic-search]]
            [gov.stockport.sonar.ingest.inbound.pipeline-stage.report :refer [->report]]))

(defn ->start-time [state]
  (assoc state :start-time (clock/now)))

(def pipeline-stages [->start-time
                      ->events
                      ->canonical-events
                      ->elastic-search
                      ->report])

(defn log-exceptions-and-continue [stage]
  (fn [state]
    (try
      (stage state)
      (catch Exception e
        (log (.getMessage e))
        state))))

(defn process-event-data [{:keys [name] :as event-data}]
  (log "processing [" name "]")
  (reduce
    (fn [state stage] (stage state))
    event-data
    (map log-exceptions-and-continue pipeline-stages)))