(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog])
  (:gen-class))

(defn invoke []
  (into []
        (map backlog/process-file (backlog/waiting-feeds))))

(defn -main [& args]
  (clojure.pprint/pprint (map :report (invoke))))