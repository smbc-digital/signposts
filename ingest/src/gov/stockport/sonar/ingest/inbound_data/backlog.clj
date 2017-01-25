(ns gov.stockport.sonar.ingest.inbound-data.backlog
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [me.raynes.fs :as fs]
            [gov.stockport.sonar.ingest.inbound-data.csv-reader :as csv-reader]
            [gov.stockport.sonar.ingest.inbound-data.events :as events]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as elastic-search]
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
      ((safe csv-reader/read-csv))
      ((safe events/csv->events))
      ((safe elastic-search/bulk-index-new))
      ((safe report))))
