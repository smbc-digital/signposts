(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]
            [me.raynes.fs :as fs])
  (:gen-class))

(defn invoke []
  (into []
        (map backlog/process-file (backlog/waiting-feeds))))

(defn result [{:keys [file valid-events rejected-events index-name]}]
  {:file (fs/base-name file)
   :valid-events (count valid-events)
   :rejects (count rejected-events)
   :index-name index-name})

(defn report [results]
  (clojure.pprint/pprint (map result results)))

(defn -main [& args]
  (report (invoke)))