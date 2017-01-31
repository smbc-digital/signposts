(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.pipeline :refer [process-event-data]])
  (:gen-class))

(defn invoke []
  (doall (feeds/process-feeds
           (partial feeds/feed-processor
                    (buffer/create-buffer {:capacity 20000 :flush-fn flusher/flush-events})))))

(defn -main [& _]
  (invoke))