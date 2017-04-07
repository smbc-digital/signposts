(ns gov.stockport.sonar.visualise.util.popper-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.popper :as p]))

(deftest poppers

  (testing "are stateful"

    (let [pop (p/poppable [1 2 3])]
      (is (= (pop) 1))
      (is (= (pop) 2))
      (is (= (pop) 3))
      (is (= (pop) nil))
      (is (= (pop) nil))))

  (testing "and can be used like a stack (LIFO)"

    (let [stack (p/poppable)]
      (is (= (stack) nil))
      (stack 1)
      (stack 2)
      (is (= (stack) 2))
      (stack 3)
      (is (= (stack) 3))
      (is (= (stack) 1))
      (is (= (stack) nil))
      (is (= (stack) nil))))

  (testing "and can start empty"

    (let [stack (p/poppable [])]
      (is (= (stack) nil))))

  (testing "and can be given a default when popping"

    (let [stack (p/poppable [] :value-when-empty :empty)]
      (is (= (stack) :empty))
      (stack 1)
      (is (= (stack) 1))
      (is (= (stack) :empty))
      (is (= (stack) :empty)))))

