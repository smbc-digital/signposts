(ns gov.stockport.sonar.ingest.inbound.flusher
  (:require [gov.stockport.sonar.ingest.inbound.events :as events]
            [gov.stockport.sonar.ingest.elastic.client :as elastic]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.elastic.event-formatter :as ef]))

; check events against the schema
; normalise valid events i.e. any pre ES optimisations
; post via ES client
; report could be sent forwards to separate service ? channel ?

(defn flush-events [{:keys [events]}]
  (let [validated (map (comp events/enhance events/normalise events/validate) events)
        valid-events (map :data (filter #(not (:error %)) validated))
        valid-qty (count valid-events)]
    (doall
      (merge
        {:valid-events valid-qty :invalid-events (- valid-qty (count valid-events))}
        (elastic/post-bulk-data
          (ef/bulk-format-events valid-events))))))
