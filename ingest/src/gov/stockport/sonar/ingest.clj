(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [overtone.at-at :as atat])
  (:gen-class))

(def single-threaded-executor (atat/mk-pool :cpu-count 1))

(defn invoke []
  (doall (feeds/process-feeds)))

(defn -main [& _]
  (let [polling-interval-ms (:poll-interval-ms @!config)]
    (log "scheduling regular job to process feed files every " polling-interval-ms " ms")
    (atat/interspaced polling-interval-ms invoke single-threaded-executor)))
