(ns gov.stockport.sonar.ingest.inbound-data.pipeline-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound-data.pipeline :as pipeline]))

(defn dummy-stage-one [state]
  (assoc state :one "one"))

(defn dummy-stage-two [state]
  (assoc state :two "two"))

(defn failing-stage [_]
  (throw (Exception. "BARF")))

(fact "should apply stages to state"
      (with-redefs [pipeline/pipeline-stages [dummy-stage-one dummy-stage-two]]
        (let [initial-state {:initial "state"}]
          (pipeline/process-event-data initial-state) => {:initial "state"
                                                          :one     "one"
                                                          :two     "two"})))

(fact "should ignore exceptions in a stage and attempt to continue with state preserved"
      (with-redefs [pipeline/pipeline-stages [dummy-stage-one failing-stage dummy-stage-two]]
        (let [initial-state {:initial "state"}]
          (pipeline/process-event-data initial-state) => {:initial "state"
                                                          :one     "one"
                                                          :two     "two"})))