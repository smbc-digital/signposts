(ns ingest.inbound-data.backlog
  (:require [ingest.config :refer [!config]]
            [me.raynes.fs :as fs]
            [ingest.inbound-data.csv-reader :as csv-reader]
            [ingest.inbound-data.events :as events]
            [ingest.client.elastic-search-client :as elastic-search]))

(defn waiting-feeds []
  (sort (fs/list-dir (fs/file (:inbound-dir @!config) "ready"))))

(defn move-to-processed [{:keys [file] :as feed}]
  (let [target (fs/file (:inbound-dir @!config) "processed" (fs/base-name file))]
    (fs/rename file target)
    feed))

(defn process-file [file]
  (-> (csv-reader/read-csv file)
      events/csv->events
      elastic-search/bulk-index-new
      move-to-processed))