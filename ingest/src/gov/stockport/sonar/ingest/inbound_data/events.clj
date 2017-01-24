(ns gov.stockport.sonar.ingest.inbound-data.events)

(defn csv->events [{:keys [csv-data] :as feed}]
  (let [headers (map keyword (first csv-data))
        events (map #(zipmap headers %) (rest csv-data))]
    (if (empty? events)
      (update feed :errors #(conj (or % []) :file-produced-no-csv-data))
      (assoc feed :event-list events))))

