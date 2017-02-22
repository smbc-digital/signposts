(ns visualise.common.ui.search-control-query-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.query.base :as qb]
            [visualise.common.ui.search-control-state :as s]
            [visualise.common.ui.search-control-query :as query]))

(defn supply [val] (fn [] val))

(fact "should turn specified search criteria into a query"
      (let [query (query/extract-query [{:get-selected-field (supply :name)
                                         :get-query          (supply "Zeshan")
                                         :get-field-type     (supply :match-text)}
                                        {:get-selected-field (supply :address)
                                         :get-query          (supply "SK4")
                                         :get-field-type     (supply :match-text)}])]
        (:query query) => (:query (-> {}
                                      (qb/with-match :name "Zeshan")
                                      (qb/with-match :address "SK4")))))

(fact "should handle age-less-than search criteria"
      (let [query (query/extract-query [{:get-selected-field (supply :dob)
                                         :get-query          (supply 22)
                                         :get-field-type     (supply :age-less-than)}])]
        (:query query) => (:query (-> {}
                                      (qb/with-age-less-than :dob 22)))))

(fact "can handle a mixture of search criteria types"
      (let [query (query/extract-query [{:get-selected-field (supply :dob)
                                         :get-query          (supply 22)
                                         :get-field-type     (supply :age-less-than)}
                                        {:get-selected-field (supply :name)
                                         :get-query          (supply "Zeshan")
                                         :get-field-type     (supply :match-text)}])]
        (:query query) => (:query (-> {}
                                      (qb/with-age-less-than :dob 22)
                                      (qb/with-match :name "Zeshan")))))

