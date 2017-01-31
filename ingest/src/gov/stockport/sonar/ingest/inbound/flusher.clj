(ns gov.stockport.sonar.ingest.inbound.flusher)

(defn flush-events [events]
  (println "Flushing... [" (count events) "]"))
