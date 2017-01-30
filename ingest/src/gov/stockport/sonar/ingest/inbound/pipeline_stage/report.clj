(ns gov.stockport.sonar.ingest.inbound.pipeline-stage.report
  (:require [gov.stockport.sonar.spec.event-spec :refer [explainer]]
            [gov.stockport.sonar.ingest.util.logging :refer [plog]]))

(defn ->report [{:keys [name valid-events rejected-events index-name] :as feed}]
  (let [report {:name          name
                :index-name    index-name
                :valid-events  (count valid-events)
                :rejects       (count rejected-events)
                :sample-errors (map explainer (take 10 rejected-events))}]
    (plog report)
    (assoc feed :report report)))