(ns gov.stockport.sonar.ingest.inbound-data.report
  (:require [me.raynes.fs :as fs]
            [gov.stockport.sonar.spec.event-spec :refer [explainer]]))

(defn report [{:keys [file valid-events rejected-events index-name] :as feed}]
  (assoc feed :report
              {:file          (fs/name file)
               :index-name    index-name
               :valid-events  (count valid-events)
               :rejects       (count rejected-events)
               :sample-errors (map explainer (take 10 rejected-events))}))

