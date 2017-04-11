(ns gov.stockport.sonar.visualise.data.merge)

(defn merge-events [locked-events new-events]
  (let [locked-ids (into #{} (map :id locked-events))]
    (concat locked-events
            (filter (fn [{:keys [id]}] (not (contains? locked-ids id))) new-events))))

