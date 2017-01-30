(ns gov.stockport.sonar.ingest.inbound.pipeline-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.pipeline :as pipeline]))

(defn dummy-stage-one [state]
  (assoc state :one "one"))

(defn dummy-stage-two [state]
  (assoc state :two "two"))

(defn failing-stage [_]
  (throw (Exception. "BARF")))

(fact "pipeline applies stages to state"
      (with-redefs [pipeline/pipeline-stages [dummy-stage-one dummy-stage-two]]
        (let [initial-state {:initial "state" :name "pipeline-test"}]
          (pipeline/process-event-data initial-state) => {:initial "state"
                                                          :name    "pipeline-test"
                                                          :one     "one"
                                                          :two     "two"})))

(fact "should ignore exceptions in a stage and attempt to continue with state preserved"
      (with-redefs [pipeline/pipeline-stages [dummy-stage-one failing-stage dummy-stage-two]]
        (let [initial-state {:initial "state" :name "pipeline-test"}]
          (pipeline/process-event-data initial-state) => {:initial "state"
                                                          :name    "pipeline-test"
                                                          :one     "one"
                                                          :two     "two"})))