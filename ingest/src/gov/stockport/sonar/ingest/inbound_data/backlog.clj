(ns gov.stockport.sonar.ingest.inbound-data.backlog
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [me.raynes.fs :as fs]
            [gov.stockport.sonar.ingest.inbound-data.csv-reader :refer [read-csv]]
            [gov.stockport.sonar.ingest.inbound-data.events :refer [csv->events events->canonical-events]]
            [gov.stockport.sonar.ingest.client.elastic-search-client :refer [bulk-index]]
            [gov.stockport.sonar.ingest.inbound-data.report :refer [report]]))

(defn waiting-feeds []
  (let [inbound-dir (:inbound-dir @!config)]
    (log "checking in [" inbound-dir "]")
    (sort (fs/list-dir (fs/file inbound-dir "ready")))))

(defn safe [func]
  (fn [arg]
    (or (try
          (func arg)
          (catch Exception e
            (log (.getMessage e))))
        arg)))

(defn process-file [file]
  (-> {:file file}
      ((safe read-csv))
      ((safe csv->events))
      ((safe events->canonical-events))
      ((safe bulk-index))
      ((safe report))))
