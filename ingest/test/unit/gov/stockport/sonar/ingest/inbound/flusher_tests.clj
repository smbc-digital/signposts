(ns gov.stockport.sonar.ingest.inbound.flusher-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]))

(facts "about flushing events"

       (fact "flushing no events passes off peacefully"
             (flusher/flush-events []) => nil)

       (fact "valid csv lines are checked against the schema"
             (flusher/flush-events [{:idx 1 :data ..data-one..}
                                    {:idx 2 :error ..some-error..}
                                    {:idx 3 :data ..data-three..}]) => nil
             (provided
               ))



       )
