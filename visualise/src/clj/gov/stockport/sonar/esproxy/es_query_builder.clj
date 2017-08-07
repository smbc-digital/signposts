(ns gov.stockport.sonar.esproxy.es-query-builder
  (:require [gov.stockport.sonar.esproxy.es-query :as es]))

(defmulti parse-query-def
          (fn [_ {:keys [field-type]}] (keyword field-type)) :default :default)

(defmethod parse-query-def :age-less-than
  [qip {:keys [query field-name]}]
  (es/with-age-less-than qip field-name query))

(defmethod parse-query-def :age-more-than
  [qip {:keys [query field-name]}]
  (es/with-age-more-than qip field-name query))

(defmethod parse-query-def :query-all
  [qip {:keys [query]}]
  (es/with-query-string qip query))

(defmethod parse-query-def :address-with-postcode
  [qip {:keys [query]}]
  (es/with-address qip query))

(defmethod parse-query-def :date-of-birth
           [qip {:keys [query field-name]}]
           (es/with-date-of-birth qip field-name query)
           )
(defmethod parse-query-def :default
  [qip {:keys [query field-name]}]
  (es/with-match qip field-name query))

(defn build-es-query [query-defs]
  (reduce
    parse-query-def
    (-> (es/query)
        (es/with-size 250))
    query-defs))
