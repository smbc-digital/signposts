(ns gov.stockport.sonar.visualise.data.merge)

(defn merge-events [locked-events new-events]
  (let [locked-ids (into #{} (map :id locked-events))]
    (concat locked-events
            (filter (fn [{:keys [id]}] (not (contains? locked-ids id))) new-events))))

(defn merge-people-flags [locked-people new-people]
  (reduce merge {}
          (map (fn [[pkey pvalue]]
                 {pkey (merge pvalue (dissoc (get locked-people pkey) :data :has-selected-event?))}) new-people)))