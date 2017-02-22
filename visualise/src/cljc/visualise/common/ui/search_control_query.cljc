(ns visualise.common.ui.search-control-query
  (:require [visualise.common.query.base :as qb]))

(defmulti criteria-to-query (fn [_ {:keys [get-field-type]}] (get-field-type)) :default :default)

(defmethod criteria-to-query :age-less-than
  [qip {:keys [get-query get-selected-field]}]
  (qb/with-age-less-than qip (get-selected-field) (get-query)))

(defmethod criteria-to-query :default
  [qip {:keys [get-query get-selected-field]}]
  (qb/with-match qip (get-selected-field) (get-query)))

(defn extract-query [search-criteria]
  (reduce
    criteria-to-query
    (-> (qb/query)
        (qb/with-size 25))
    search-criteria))

