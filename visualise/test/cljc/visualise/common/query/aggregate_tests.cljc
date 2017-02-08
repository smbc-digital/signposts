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

(fact "summarises single level aggregation"
      (qa/summarise
        {:aggregations {:a {:buckets [{:key "GMP" :doc_count 1}
                                      {:key "HOMES" :doc_count 3}]}}}) => {:a {:results {:GMP   1
                                                                                         :HOMES 3}}})

(fact "summarises multi level aggregation"
      (qa/summarise
        {:aggregations {:a {:buckets [{:key       "GMP"
                                       :doc_count 3
                                       :b         {:buckets [{:key "CAUTION" :doc_count 2}
                                                             {:key "ASBO" :doc_count 1}]}}
                                      {:key       "HOMES"
                                       :doc_count 5
                                       :b         {:buckets [{:key "CAUTION" :doc_count 3}
                                                             {:key "ASBO" :doc_count 2}]}}]}}}) => {:GMP   {}
                                                                                                    :HOMES 3})