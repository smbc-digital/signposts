(ns gov.stockport.sonar.esproxy.es-query-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.es-query :as es]))

(fact "builds query that requests no records"
      (-> (es/query)
          (es/with-no-results)) => {:size 0})

(fact "can build query string query"
      (es/with-query-string {} "some-search-term") => {:query {:bool {:must [{:query_string {:query         "some-search-term"
                                                                                             :default_field "_all"}}]}}})
(fact "can max-age query"
      (es/with-max-age {} 20) => {:query {:bool {:must [{:range {:dob {:gte "now-20y"}}}]}}})

(fact "can query for term"
      (es/with-term {} :some-field "some value") => {:query {:bool {:must [{:term {:some-field {:value "some value"}}}]}}})

(fact "can query for a match"
      (es/with-match {} :some-field "some value") => {:query {:bool {:must [{:match {:some-field {:query    "some value"
                                                                                                  :operator :and}}}]}}})

(fact "can query for age less than"
      (es/with-age-less-than {} :some-field 22) => {:query {:bool {:must [{:range {:some-field {:gte "now-22y"}}}]}}})

(fact "can query for age more than"
      (es/with-age-more-than {} :some-field 10) => {:query {:bool {:must [{:range {:some-field {:lte "now-10y"}}}]}}})

(fact "can query for address with postcode"
      (es/with-address {} "some value") => {:query {:bool {:must [{:bool {:should               [{:match {:address {:query    "some value"
                                                                                                                    :operator :and}}}
                                                                                                 {:match {:postcode {:query    "some value"
                                                                                                                     :operator :and}}}]
                                                                          :minimum_should_match 1}}]}}})

(fact "can combine queries"
      (-> (es/query)
          (es/with-size 15)
          (es/with-query-string "argh")
          (es/with-max-age 20)) => {:size 15
                                    :query
                                          {:bool
                                           {:must
                                            [{:query_string {:query         "argh"
                                                             :default_field "_all"}}
                                             {:range {:dob {:gte "now-20y"}}}]}}})
