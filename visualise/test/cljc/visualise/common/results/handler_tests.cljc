(ns visualise.common.results.handler-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.results.handler :as h]))

(fact "should store results"
      (let [!state (atom {})
            handler (h/default-handler !state)
            _ (handler ..some-results..)]
        (:results @!state) => ..some-results..))
