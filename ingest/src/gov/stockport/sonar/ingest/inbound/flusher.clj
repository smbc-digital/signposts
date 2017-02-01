(ns gov.stockport.sonar.ingest.inbound.flusher
  (:require [gov.stockport.sonar.ingest.inbound.events :as events]))

; check events against the schema
; normalise valid events i.e. any pre ES optimisations
; post via ES client
; report could be sent forwards to separate service ? channel ?

(defn flush-events [events]
  (doall
    (println (str "Flush " (count (filter #(not (:error %)) (map events/validate events)))))))
