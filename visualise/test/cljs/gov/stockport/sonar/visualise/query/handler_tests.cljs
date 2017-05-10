(ns gov.stockport.sonar.visualise.query.handler-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]))

(deftest results-handler-tests

  (testing "extracts the core results data with score"
    (let [!state (atom {})
          handler (h/default-handler !state)
          _ (handler {:took 99
                      :hits {:total 1234
                             :hits  [{:_score  1.2
                                      :_source {:timestamp "2012-12-28T13:14:15.000Z"}}]}})]

      (is (= (:took-millis @!state) 99))
      (is (= (:total @!state) 1234))
      (is (t/= (:timestamp (first (:result @!state))) (t/date-time 2012 12 28 13 14 15)))
      (is (= (:score (first (:result @!state))) 1.2))))

  (testing "adds information about individuals and people in the dataset"
    (with-redefs [people/from-data (fn [m] (merge m {:people :some-people :show-only-highlighted? true}))
                  timespan/from-data (fn [_] :some-timespan)]
                 (let [!state (atom {})
                       handler (h/default-handler !state)
                       _ (handler {:took 99
                                   :hits {:total 1234
                                          :hits  [{:_source {}}]}})]
                   (is (= (:people @!state) :some-people))
                   (is (= (:show-only-highlighted? @!state) true))
                   (is (= (:timespan @!state) :some-timespan)))))

  (testing "replaces current dataset with new people"
    (with-redefs [people/from-data (fn [& _] {:people :some-different-people :show-only-highlighted? true})
                  timespan/from-data (fn [& _] {})]
                 (let [!state (atom {:people                 :some-people
                                     :show-only-highlighted? false})
                       handler (h/default-handler !state)
                       _ (handler {:took 99
                                   :hits {:total 1234
                                          :hits  [{:_source {}}]}})
                       ]
                   (is (= (:people @!state) :some-different-people))
                   (is (= (:show-only-highlighted? @!state) true))))))