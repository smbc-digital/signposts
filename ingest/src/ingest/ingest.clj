(ns ingest.ingest
  (:require [ingest.config :refer [!config]]
            [ingest.inbound-data.backlog :as backlog]))

(defn invoke []
  (into []
        (map backlog/process-file (backlog/waiting-feeds))))