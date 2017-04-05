(ns gov.stockport.sonar.visualise.util.popper-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.popper :as p]))

(deftest poppers

         (testing "are stateful (poppable)"

                  (let [pop (p/poppable [1 2 3])]
                    (is (= (pop) 1))
                    (is (= (pop) 2))
                    (is (= (pop) 3))
                    (is (= (pop) nil))
                    (is (= (pop) nil)))))

