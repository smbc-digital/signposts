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
      (qb/with-term {} :some-field "some value") => {:query {:bool {:must [{:term {:some-field {:value "some value"}}}]}}})

(fact "can query for a match"
      (qb/with-match {} :some-field "some value") => {:query {:bool {:must [{:match {:some-field "some value"}}]}}})

(fact "can query for age less than"
      (qb/with-age-less-than {} :some-field 22) => {:query {:bool {:must [{:range {:some-field {:gte "now-22y"}}}]}}})

(fact "can query for age more than"
      (qb/with-age-more-than {} :some-field 10) => {:query {:bool {:must [{:range {:some-field {:lte "now-10y"}}}]}}})

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
