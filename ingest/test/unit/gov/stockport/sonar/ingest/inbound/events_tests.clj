(ns gov.stockport.sonar.ingest.inbound.events-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.events :as events]))

(def valid-event {:idx 1 :data {:event-source "SOURCE"
                                :event-type   "TYPE"
                                :timestamp    "2017-01-01T12:12:12.000Z"
                                :some-key     "SOME-VAL"}})

(facts "about event validation"

       (fact "valid event is returned unchanged if validation occurs"
             (events/validate valid-event) => valid-event)

       (fact "invalid event is returned with spec error indicated and original event data preserved"
             (let [{:keys [error data]} (events/validate {:idx 1 :data {:event-source "SOURCE"}})]
               error => :event-spec-validation
               data => {:event-source "SOURCE"}))

       (fact "event already invalid is ignored and returned"
             (events/validate {:idx 1 :data {} :error :some-error}) => {:idx 1 :data {} :error :some-error}))

