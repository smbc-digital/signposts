(ns gov.stockport.sonar.visualise.data.merge-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.merge :as merge]))

(deftest merge-tests

  (testing "event merging"

    (testing "merge two empty lists of events"
      (is (= (merge/merge-events [] []) [])))

    (testing "merge new events into empty list"
      (is (= (merge/merge-events [] [{:id 1 :data 1}]) [{:id 1 :data 1}])))

    (testing "merge new events onto locked events"
      (is (= (merge/merge-events [{:id 1 :data 1}] [{:id 1 :data 1}]) [{:id 1 :data 1}])))

    (testing "in the strange case that the new event has updated data we keep the locked one"
      (is (= (merge/merge-events [{:id 1 :data 1}
                                  {:id 2 :data 2}]

                                 [{:id 1 :data 10}
                                  {:id 3 :data 3}])

             [{:id 1 :data 1}
              {:id 2 :data 2}
              {:id 3 :data 3}]))))

  (testing "people flags merging"

    (testing "merge two empty sets of people"
      (is (= (merge/merge-people-flags {} {}) {})))

    (testing "merge people flags"
      (is (= (merge/merge-people-flags {{:name "N1"} {:data   "irrelevant"
                                                :highlighted? true
                                                :other-flag?  true}}
                                       {{:name "N1"} {:data "DATA-N1"}
                                  {:name "N2"} {:data "DATA-N2"}})
             {{:name "N1"} {:data         "DATA-N1"
                            :highlighted? true
                            :other-flag?  true}
              {:name "N2"} {:data "DATA-N2"}})))))

