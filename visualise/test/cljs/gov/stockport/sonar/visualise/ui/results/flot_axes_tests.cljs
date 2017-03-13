(ns gov.stockport.sonar.visualise.ui.results.flot-axes-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]))

(def single-event [{:timestamp (t/date-time 2016 12 1)}])

(def multiple-events [{:timestamp (t/date-time 2016 11 1)}
                      {:timestamp (t/date-time 2016 12 1)}])

(deftest flot-axes

  (testing "x-axis defaults are set"
    (let [result (fa/x-axis single-event)]
      (is (= (:mode result) "time"))
      (is (= (:timeFormat result) "%Y/%m/%d"))
      (is (= (:minTickSize result) [1 "month"]))))

  (testing "x-axis includes a buffer at each end for a single event"
    (let [result (fa/x-axis single-event)]
      (is (t/= (:min result) (t/date-time 2016 11)))
      (is (t/= (:max result) (t/date-time 2017 1)))))

  (testing "x-axis includes a buffer at each end for multiple events"
    (let [result (fa/x-axis multiple-events)]
      (is (t/= (:min result) (t/date-time 2016 10)))
      (is (t/= (:max result) (t/date-time 2017 1))))))