(ns gov.stockport.sonar.visualise.util.date-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.util.date :as d]))

(deftest date-tests

  (with-redefs
    [t/now (fn [] (t/date-time 2012 2 15))]

    (testing "human readable elapsed"
      (is (= (d/human-since (t/date-time 2012 2 15 0 0 0)) "Today"))
      (is (= (d/human-since (t/date-time 2012 2 15 23 59 59)) "Today"))
      (is (= (d/human-since (t/date-time 2012 2 14 0 0 0)) "Yesterday"))
      (is (= (d/human-since (t/date-time 2012 2 14 23 59 59)) "Yesterday"))
      (is (= (d/human-since (t/date-time 2012 2 13)) "2 days ago"))
      (is (= (d/human-since (t/date-time 2012 2 9)) "6 days ago"))
      (is (= (d/human-since (t/date-time 2012 2 8)) "1 week ago"))
      (is (= (d/human-since (t/date-time 2012 2 1)) "2 weeks ago"))
      (is (= (d/human-since (t/date-time 2011 2 1)) "54 weeks ago"))))

  (testing "simple format"
    (is (= (d/date-format (t/date-time 2012 3 9)) "9 Mar 2012"))))


