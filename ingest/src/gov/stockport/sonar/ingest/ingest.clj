(ns gov.stockport.sonar.ingest.ingest
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]))

(defn invoke []
  (into []
        (map backlog/process-file (backlog/waiting-feeds))))