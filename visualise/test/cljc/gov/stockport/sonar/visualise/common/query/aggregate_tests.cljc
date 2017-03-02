(ns gov.stockport.sonar.visualise.common.query.aggregate-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.common.query.aggregate :as qa]))

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