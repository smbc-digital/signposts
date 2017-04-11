(ns gov.stockport.sonar.visualise.data.timespan-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]
            [cljs-time.core :as t]))

(deftest timespan-tests

  (testing "extracts timespan for single event including buffer"
    (let [{:keys [from-date to-date]}
          (timespan/from-data {:result [{:timestamp (t/date-time 2017)}]})]

      (is (t/= from-date (t/date-time 2016 12)))
      (is (t/= to-date (t/date-time 2017 2)))))

  (testing "extracts timespan from multiple events including buffer"
    (let [{:keys [from-date to-date selected-from selected-to]}
          (timespan/from-data {:result [{:timestamp (t/date-time 2016)} {:timestamp (t/date-time 2017)}]})]

      (is (t/= from-date (t/date-time 2015 12)))
      (is (t/= to-date) (t/date-time 2017 2))
      (is (t/= selected-from (t/date-time 2015 12)))
      (is (t/= selected-to) (t/date-time 2017 2))
      )))