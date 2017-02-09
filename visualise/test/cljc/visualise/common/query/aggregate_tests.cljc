(ns visualise.common.query.aggregate-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.query.aggregate :as qa]))

(fact "builds query with different bits"
      (-> (qa/query)
          (qa/with-no-results)
          (qa/with-term-aggregation :a)) => {:size 0
                                             :aggs {:a {:terms {:field :a :size 100}}}})

(fact "builds terms query for single term"
      (qa/with-term-aggregation {} :event-source.keyword) =>
      {:aggs {:event-source.keyword {:terms {:field :event-source.keyword :size 100}}}})

(fact "builds terms query for two terms"
      (qa/with-term-aggregation {} :event-source.keyword :event-type.keyword) =>
      {:aggs {:event-source.keyword {:terms {:field :event-source.keyword :size 100}
                                     :aggs  {:event-type.keyword {:terms {:field :event-type.keyword :size 100}}}}}})

(fact "builds terms query for three terms"
      (qa/with-term-aggregation {} :a :b :c) =>
      {:aggs {:a {:terms {:field :a :size 100}
                  :aggs  {:b {:terms {:field :b :size 100}
                              :aggs  {:c {:terms {:field :c :size 100}}}}}}}})

(fact "can build query string query"
      (qa/with-query-string {} "some-search-term") => {:query {:bool {:must [{:query-string {:query         "some-search-term"
                                                                                             :default_field "_all"}}]}}})
(fact "can max-age query"
      (qa/with-max-age {} 20) => {:query {:bool {:must [{:range {:dob {:gte "now-20y"}}}]}}})

(fact "can combine queries"
      (-> {}
          (qa/with-query-string "argh")
          (qa/with-max-age 20)) => {:query
                                    {:bool
                                     {:must
                                      [{:query_string {:query         "argh"
                                                       :default_field "_all"}}
                                       {:range {:dob {:gte "now-20y"}}}
                                       ]}}})
