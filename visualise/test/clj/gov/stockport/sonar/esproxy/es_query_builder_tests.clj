(ns gov.stockport.sonar.esproxy.es-query-builder-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.es-query :as es-query]
            [gov.stockport.sonar.esproxy.es-query-builder :as es-query-builder]))

(facts "about query building"

       (fact "should turn specified search criteria into a query"
             (let [query (es-query-builder/build-es-query [{:field-type :match-text
                                                            :field-name :name
                                                            :query      "Zeshan"}
                                                           {:field-type :match-text
                                                            :field-name :address
                                                            :query      "SK4"}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-match :name "Zeshan")
                                             (es-query/with-match :address "SK4")))))

       (fact "can handle a mixture of search criteria types"
             (let [query (es-query-builder/build-es-query [{:field-name :dob
                                                            :query      22
                                                            :field-type :age-less-than}
                                                           {:field-name :name
                                                            :query      "Zeshan"
                                                            :field-type :match-text}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-age-less-than :dob 22)
                                             (es-query/with-match :name "Zeshan")))))

       (fact "should handle age-less-than search criteria"
             (let [query (es-query-builder/build-es-query [{:field-name :dob
                                                            :query      22
                                                            :field-type :age-less-than}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-age-less-than :dob 22)))))

       (fact "should handle all fields query"
             (let [query (es-query-builder/build-es-query [{:query      "SK2"
                                                            :field-type :query-all}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-query-string "SK2")))))

       (fact "should handle address query"
             (let [query (es-query-builder/build-es-query [{:query      "SK2"
                                                            :field-type :address-with-postcode}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-address "SK2")))))


       (fact "should handle age-more-than search criteria"
             (let [query (es-query-builder/build-es-query [{:field-name :dob
                                                            :query      12
                                                            :field-type :age-more-than}])]
               (:query query) => (:query (-> {}
                                             (es-query/with-age-more-than :dob 12))))))