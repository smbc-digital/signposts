(ns gov.stockport.sonar.ingest.inbound.flusher-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [gov.stockport.sonar.ingest.inbound.events :as events]))

(facts "about flushing events"

       (fact "flushing no events passes off peacefully"
             (flusher/flush-events []) => nil)

       (fact "valid csv lines are checked against the schema"
             (flusher/flush-events [{:idx 1 :data ..data-one..}
                                    {:idx 2 :error ..some-error..}
                                    {:idx 3 :data ..data-three..}]) => nil
             (provided
               (events/validate {:idx 1 :data ..data-one..}) => {:idx 1 :data ..data-one..}
               (events/validate {:idx 2 :error ..some-error..}) => {:idx 2 :error ..some-error..}
               (events/validate {:idx 3 :data ..data-three..}) => {:idx 1 :data ..data-one.. :error ..some-error..}
               ))

       )
