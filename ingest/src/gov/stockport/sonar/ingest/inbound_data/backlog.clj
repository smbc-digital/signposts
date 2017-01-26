(ns gov.stockport.sonar.ingest.inbound-data.backlog
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [me.raynes.fs :as fs]
            [clojure.java.io :as io]))

(defn list-waiting-csv-files []
  (let [inbound-dir (:inbound-dir @!config)]
    (log "checking in [" inbound-dir "]")
    (map
      (fn [file]
        {:file   file
         :name   (fs/base-name file)
         :stream (io/reader file)})
      (sort (fs/list-dir (fs/file inbound-dir "ready"))))))