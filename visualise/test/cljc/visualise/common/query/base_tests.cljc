(ns visualise.common.query.base-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.query.base :as qb]))

(fact "builds query that requests no records"
      (-> (qb/query)
          (qb/with-no-results)) => {:size 0})

(fact "can build query string query"
      (qb/with-query-string {} "some-search-term") => {:query {:bool {:must [{:query_string {:query         "some-search-term"
                                                                                             :default_field "_all"}}]}}})
(fact "can max-age query"
      (qb/with-max-age {} 20) => {:query {:bool {:must [{:range {:dob {:gte "now-20y"}}}]}}})

(fact "can query for term"
      (qb/with-field {} :some-field "some value") => {:query {:bool {:must [{:term {:some-field {:value "some value"}}}]}}})

(fact "can combine queries"
      (-> (qb/query)
          (qb/with-size 15)
          (qb/with-query-string "argh")
          (qb/with-max-age 20)) => {:size 15
                                    :query
                                          {:bool
                                           {:must
                                            [{:query_string {:query         "argh"
                                                             :default_field "_all"}}
                                             {:range {:dob {:gte "now-20y"}}}]}}})
