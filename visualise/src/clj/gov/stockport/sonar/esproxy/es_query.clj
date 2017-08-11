(ns gov.stockport.sonar.esproxy.es-query
  (:require [clj-time.format :as tf]
             [gov.stockport.sonar.esproxy.es-query-parser :as esp]))

(def uk-date-format  (tf/formatter "dd/mm/yyyy"))
(def iso-date-format (tf/formatter "yyyy-mm-dd"))

(defn query []
  {})

(defn format-date [date]
  (tf/unparse iso-date-format (tf/parse uk-date-format date))
  )

(defn with-no-results [qip]
  (assoc qip :size 0))

(defn with-size [qip number]
  (assoc qip :size number))

(defn- ensure-clauses-at [qip path]
  (update-in qip path #(or % [])))

(defn- ensure-clauses-at-wildcard [qip path]
  (update-in qip path #(or % {})))


(defn add-to [qip path clause]
  (update-in (ensure-clauses-at qip path) path #(conj % clause)))

(defn add-to-wildcard  [qip path clause]
  (update-in (ensure-clauses-at-wildcard qip path) path #(conj % clause)))

(defn must [qip clause]
  (add-to qip [:query :bool :must] clause))

(defn should [qip clause]
  (add-to qip [:query :bool :should] clause))

(defn should-wildcard[qip clause]
  (add-to-wildcard qip [:query :bool] clause))

(defn with-query-string [qip qs]
  (must qip {:query_string {:query qs :default_field "_all"}}))

(defn with-max-age [qip max-age]
  (must qip {:range {:dob {:gte (str "now-" max-age "y")}}}))

(defn with-term [qip term value]
  (must qip {:term {term {:value value}}}))

(defn with-match [qip term value]
  (must qip {:match {term {:query    value
                           :operator :and}}}))

(defn with-date-of-birth [qip term value]
  (must qip {:match { term (format-date value)}}))


(defn with-age-less-than [qip term value]
  (must qip {:range {term {:gte (str "now-" value "y")}}}))

(defn with-age-more-than [qip term value]
  (must qip {:range {term {:lte (str "now-" value "y")}}}))

(defn with-address [qip value]
  (-> qip
      (must {:bool {:should               [{:match {:address {:query    value
                                                              :operator :and}}}
                                           {:match {:postcode {:query    value
                                                               :operator :and}}}]
                    :minimum_should_match 1}})))

(defn wildcard [qip term value]
      (should-wildcard qip (esp/parse-query term value)

  ))