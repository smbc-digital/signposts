(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.pipeline :refer [process-event-data]])
  (:gen-class))

(defn invoke []
  (doall (feeds/process-feeds)))

(defn -main [& _]
  (invoke))