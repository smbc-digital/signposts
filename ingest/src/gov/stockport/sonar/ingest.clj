(ns gov.stockport.sonar.ingest
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :refer [list-waiting-csv-files]]
            [gov.stockport.sonar.ingest.inbound-data.pipeline :refer [process-event-data]])
  (:gen-class))

(defn invoke []
  (into []
        (map process-event-data
             (list-waiting-csv-files))))

(defn invoke-and-report []
  (doall
    (map
      #(clojure.pprint/pprint (:report (process-event-data %)))
      (list-waiting-csv-files))))

(defn -main [& _]
  (invoke-and-report))