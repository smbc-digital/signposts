(ns visualise.common.results.handler-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.util.foreign :as d]
            [visualise.common.results.handler :as h]))

(fact "should store results"
      (let [!state (atom {})
            handler (h/default-handler !state)
            _ (handler {:took 99
                        :hits {:total 1234
                               :hits  [{:_source {:timestamp ..ts..}}]}})]
        (:took-millis @!state) => 99
        (:total @!state) => 1234
        (:result @!state) => [{:timestamp ..pts..}]
        (provided
          (d/parse-timestamp ..ts..) => ..pts..)))
