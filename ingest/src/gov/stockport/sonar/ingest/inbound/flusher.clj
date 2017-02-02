(ns gov.stockport.sonar.ingest.inbound.flusher
  (:require [gov.stockport.sonar.ingest.inbound.events :as events]
            [gov.stockport.sonar.ingest.elastic.client :as elastic]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.elastic.event-formatter :as ef]))

; check events against the schema
; normalise valid events i.e. any pre ES optimisations
; post via ES client
; report could be sent forwards to separate service ? channel ?

(defn flush-events [{:keys [events feed-hash]}]
  (doall
    (elastic/post-bulk-data
      (ef/bulk-format-events
        feed-hash
        (map :data
             (filter #(not (:error %)) (map events/validate events)))))))
