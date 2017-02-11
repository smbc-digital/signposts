(ns gov.stockport.sonar.ingest.inbound.pipeline-stage.report
  (:require [gov.stockport.sonar.spec.event-spec :refer [explainer]]
            [gov.stockport.sonar.ingest.util.logging :refer [plog]]
            [clj-time.core :as t]
            [gov.stockport.sonar.ingest.clock :as clock]))

(defn ->report [{:keys [name start-time valid-events rejected-events index-name] :as feed}]
  (let [report {:name            name
                :index-name      index-name
                :elapsed-time-ms (t/in-millis (t/interval start-time (clock/now)))
                :valid-events    (count valid-events)
                :rejects         (count rejected-events)
                :sample-errors   (map explainer (take 10 rejected-events))}]
    (plog report)
    (assoc feed :report report)))