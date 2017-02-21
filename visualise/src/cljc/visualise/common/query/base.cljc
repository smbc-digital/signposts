(ns visualise.common.query.base)

(defn query []
  {})

(defn with-no-results [qip]
  (assoc qip :size 0))

(defn with-size [qip number]
  (assoc qip :size number))

(defn ensure-clauses-at [qip path]
  (update-in qip path #(or % [])))

(defn add-to [qip path clause]
  (update-in (ensure-clauses-at qip path) path #(conj % clause)))

(defn must [qip clause]
  (add-to qip [:query :bool :must] clause))

(defn with-query-string [qip qs]
  (must qip {:query_string {:query qs :default_field "_all"}}))

(defn with-max-age [qip max-age]
  (must qip {:range {:dob {:gte (str "now-" max-age "y")}}}))

(defn with-field [qip term value]
  (must qip {:term {term {:value value}}}))
