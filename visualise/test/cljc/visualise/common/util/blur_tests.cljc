(ns visualise.common.util.blur-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.util.blur :as b]))

(fact "should produce a blur of numbers"
      (let [error 1/1000]
        (b/blurred 2 1) => (just [(roughly 2.0 error)])

        (b/blurred 2 3) => (just [(roughly 1.95 error)
                                  (roughly 2.0 error)
                                  (roughly 2.05 error)])

        (b/blurred 2 4) => (just [(roughly 1.925 error)
                                  (roughly 1.975 error)
                                  (roughly 2.025 error)
                                  (roughly 2.075 error)])))

