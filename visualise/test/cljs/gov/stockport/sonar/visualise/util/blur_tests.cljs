(ns gov.stockport.sonar.visualise.util.blur-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.blur :as b]))

(defn within-error [error-margin]
  (fn [[v1 v2]]
    (< (.abs js/Math (- v1 v2)) error-margin)))

(defn =ish [list-one list-two]
  (and (= (count list-one) (count list-two))
       (every? (within-error 0.001) (partition 2 (interleave list-one list-two)))))

(deftest test-our-blur-test-helpers
  (testing "should check for approximate equality"
    (let [checker (within-error 0.2)]
      (is (= (checker [2 1.8]) true))
      (is (= (checker [1.8 2]) true))
      (is (= (checker [2 1.7]) false))
      (is (= (checker [1.7 2]) false))))

  (testing "should check for approximate list equality"
    (is (= (=ish [1 2 3] [1 2 3 4]) false))
    (is (= (=ish [1 2 3] [1 2 4]) false))
    (is (= (=ish [1 2 3] [1.001 1.999 3.0]) true))))

(deftest blur-tests

  (testing "should produce a blur of numbers"

    (let [blurrer (b/blurrer 1)]

      (is (=ish (blurrer 2 0)
             []))

      (is (=ish (blurrer 2 1)
             [2]))

      (is (=ish (blurrer 2 3)
             [1 2 3]))

      (is (=ish (blurrer 2 4)
             [0.5 1.5 2.5 3.5]))))

  (testing "should limit the blurring of numbers"
    (let [blurrer (b/blurrer 0.1)
          limited-blurrer (b/blurrer 0.1 0.2)]

      (is (=ish (blurrer 2 2)
                [1.95 2.05]))

      (is (=ish (limited-blurrer 2 2)
                [1.95 2.05]))

      (is (=ish (blurrer 2 6)
                [1.75 1.85 1.95 2.05 2.15 2.25]))

      (is (=ish (limited-blurrer 2 6)
                [1.8 1.85 1.95 2.05 2.15 2.2]))

      (is (=ish (limited-blurrer 2 8)
                [1.8 1.8 1.85 1.95 2.05 2.15 2.2 2.2])))))




