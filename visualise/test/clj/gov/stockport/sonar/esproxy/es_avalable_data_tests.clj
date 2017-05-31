(ns gov.stockport.sonar.esproxy.es-avalable-data-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.es-available-data :as ad]))

(def example-result
  {:aggregations
   {:event-source
    {:doc_count_error_upper_bound 0,
     :sum_other_doc_count         0,
     :buckets                     [{:key        "GMP",
                                    :doc_count  29726,
                                    :event-type {:buckets [{:key            "CAUTION",
                                                            :doc_count      14954,
                                                            :loaded         {:value_as_string :gmp-caution-loaded},
                                                            :timestamp-to   {:value_as_string :gmp-caution-to},
                                                            :timestamp-from {:value_as_string :gmp-caution-from}}
                                                           {:key            "ASBO",
                                                            :doc_count      14772,
                                                            :loaded         {:value_as_string :gmp-asbo-loaded},
                                                            :timestamp-to   {:value_as_string :gmp-asbo-to},
                                                            :timestamp-from {:value_as_string :gmp-asbo-from}}]}}
                                   {:key        "EIS",
                                    :doc_count  8011,
                                    :event-type {:buckets [{:key            "LAC",
                                                            :doc_count      4048,
                                                            :loaded         {:value_as_string :eis-lac-loaded},
                                                            :timestamp-to   {:value_as_string :eis-lac-to},
                                                            :timestamp-from {:value_as_string :eis-lac-from}}
                                                           {:key            "CIN",
                                                            :doc_count      3963,
                                                            :loaded         {:value_as_string :eis-cin-loaded},
                                                            :timestamp-to   {:value_as_string :eis-cin-to},
                                                            :timestamp-from {:value_as_string :eis-cin-from}}]}}]}}})

(fact "should summarise available data from the es aggregation result"
      (ad/summarise-available-data example-result) => [{:event-source "GMP"
                                                        :event-type   "CAUTION"
                                                        :qty          14954
                                                        :from         :gmp-caution-from
                                                        :to           :gmp-caution-to
                                                        :last-updated :gmp-caution-loaded}
                                                       {:event-source "GMP"
                                                        :event-type   "ASBO"
                                                        :qty          14772
                                                        :from         :gmp-asbo-from
                                                        :to           :gmp-asbo-to
                                                        :last-updated :gmp-asbo-loaded}
                                                       {:event-source "EIS"
                                                        :event-type   "LAC"
                                                        :qty          4048
                                                        :from         :eis-lac-from
                                                        :to           :eis-lac-to
                                                        :last-updated :eis-lac-loaded}
                                                       {:event-source "EIS"
                                                        :event-type   "CIN"
                                                        :qty          3963
                                                        :from         :eis-cin-from
                                                        :to           :eis-cin-to
                                                        :last-updated :eis-cin-loaded}])
