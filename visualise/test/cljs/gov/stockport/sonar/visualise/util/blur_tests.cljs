(ns gov.stockport.sonar.visualise.util.blur-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.blur :as b]))

(deftest blur-tests

  (testing "should produce a blur of numbers"

    (let [blurrer (b/blurrer 1)]

      (is (= (blurrer 2 0)
          []))

      (is (= (blurrer 2 1)
             [2]))

      (is (= (blurrer 2 3)
             [1 2 3]))

      (is (= (blurrer 2 4)
             [0.5 1.5 2.5 3.5])))))




