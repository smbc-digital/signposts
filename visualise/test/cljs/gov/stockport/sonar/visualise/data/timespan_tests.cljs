(ns gov.stockport.sonar.visualise.data.timespan-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]
            [cljs-time.core :as t]))

(deftest timespan-tests

  (testing "ignores situations where there is no data"
    (is (nil? (timespan/from-events []))))

  (testing "ignore events with no timestamp"
    (let [{:keys [from-date to-date]}
          (timespan/from-events [{:timestamp (t/date-time 2016)} {:something :else}])]
      (is (t/= from-date (t/date-time 2015 12)))
      (is (t/= to-date) (t/date-time 2017 2))))

  (testing "extracts timespan for single event including buffer"
    (let [{:keys [from-date to-date]}
          (timespan/from-events [{:timestamp (t/date-time 2016)}])]
      (is (t/= from-date (t/date-time 2015 12)))
      (is (t/= to-date) (t/date-time 2017 2))))

  (testing "extracts timestamp from multiple events"
    (let [{:keys [from-date to-date]}
          (timespan/from-events [{:timestamp (t/date-time 2016)}
                                 {:timestamp (t/date-time 2017)}])]
      (is (t/= from-date (t/date-time 2015 12)))
      (is (t/= to-date) (t/date-time 2017 2)))))