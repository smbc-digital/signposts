(ns gov.stockport.sonar.ingest.inbound.flusher
  (:require [gov.stockport.sonar.ingest.inbound.events :as events]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]
            [gov.stockport.sonar.ingest.elastic.event-formatter :as ef]))

; check events against the schema
; normalise valid events i.e. any pre ES optimisations
; post via ES client
; report could be sent forwards to separate service ? channel ?

(defn flush-events [events]
  (doall
    (esc/post-bulk-data-to-es
      (ef/bulk-format-events
        (map :data
             (filter #(not (:error %)) (map events/validate events)))))))
