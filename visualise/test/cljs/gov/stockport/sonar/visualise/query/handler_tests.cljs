(ns gov.stockport.sonar.visualise.query.handler-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.common.results.individuals :as i]))

(deftest results-handler-tests

  (testing "extracts the core results data"
    (let [!state (atom {})
          handler (h/default-handler !state)
          _ (handler {:took 99
                      :hits {:total 1234
                             :hits [{:_source {:timestamp "2012-12-28T13:14:15.000Z"}}]}})]

      (is (= (:took-millis @!state) 99))
      (is (= (:total @!state) 1234))
      (is (t/= (:timestamp (first (:result @!state))) (t/date-time 2012 12 28 13 14 15)))))

  (testing "adds information about individuals in the dataset"
    (with-redefs [i/individuals (fn [_] :some-individuals)]
    (let [!state (atom {})
          handler (h/default-handler !state)
          _ (handler {:took 99
                      :hits {:total 1234
                             :hits [{:_source {}}]}})]
      (is (= (:individuals @!state) :some-individuals))))))

