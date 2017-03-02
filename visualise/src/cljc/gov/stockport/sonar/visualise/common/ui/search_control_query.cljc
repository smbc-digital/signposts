(ns gov.stockport.sonar.visualise.common.ui.search-control-query
  (:require [gov.stockport.sonar.visualise.common.query.base :as qb]))

(defmulti criteria-to-query (fn [_ {:keys [get-field-type]}] (get-field-type)) :default :default)

(defmethod criteria-to-query :age-less-than
  [qip {:keys [get-query get-field-name]}]
  (qb/with-age-less-than qip (get-field-name) (get-query)))

(defmethod criteria-to-query :age-more-than
  [qip {:keys [get-query get-field-name]}]
  (qb/with-age-more-than qip (get-field-name) (get-query)))

(defmethod criteria-to-query :query-all
  [qip {:keys [get-query]}]
  (qb/with-query-string qip (get-query)))

(defmethod criteria-to-query :address-with-postcode
  [qip {:keys [get-query]}]
  (qb/with-address qip (get-query)))

(defmethod criteria-to-query :default
  [qip {:keys [get-query get-field-name]}]
  (qb/with-match qip (get-field-name) (get-query)))

(defn extract-query [search-criteria]
  (reduce
    criteria-to-query
    (-> (qb/query)
        (qb/with-size 250))
    search-criteria))

