(ns gov.stockport.sonar.visualise.common.query.base)

(defn query []
  {})

(defn with-no-results [qip]
  (assoc qip :size 0))

(defn with-size [qip number]
  (assoc qip :size number))

(defn- ensure-clauses-at [qip path]
  (update-in qip path #(or % [])))

(defn add-to [qip path clause]
  (update-in (ensure-clauses-at qip path) path #(conj % clause)))

(defn must [qip clause]
  (add-to qip [:query :bool :must] clause))

(defn should [qip clause]
  (add-to qip [:query :bool :should] clause))

(defn with-query-string [qip qs]
  (must qip {:query_string {:query qs :default_field "_all"}}))

(defn with-max-age [qip max-age]
  (must qip {:range {:dob {:gte (str "now-" max-age "y")}}}))

(defn with-term [qip term value]
  (must qip {:term {term {:value value}}}))

(defn with-match [qip term value]
  (must qip {:match {term value}}))

(defn with-age-less-than [qip term value]
  (must qip {:range {term {:gte (str "now-" value "y")}}}))

(defn with-age-more-than [qip term value]
  (must qip {:range {term {:lte (str "now-" value "y")}}}))

(defn with-address [qip value]
  (-> qip
      (must {:bool {:should               [{:match {:address value}}
                                           {:match {:postcode value}}]
                    :minimum_should_match 1}})))