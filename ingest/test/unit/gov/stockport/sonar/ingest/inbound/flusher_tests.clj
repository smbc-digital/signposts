(ns gov.stockport.sonar.ingest.inbound.flusher-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [gov.stockport.sonar.ingest.inbound.events :as events]))

; TODO
;(facts "about flushing events"
;
;       (fact "flushing no events passes off peacefully"
;             (flusher/flush-events []) => nil)
;
;       (fact "valid csv lines are checked against the schema"
;             (flusher/flush-events [{:line-number 1 :data ..data-one..}
;                                    {:line-number 2 :error ..some-error..}
;                                    {:line-number 3 :data ..data-three..}]) => nil
;             (provided
;               (events/validate {:line-number 1 :data ..data-one..}) => {:line-number 1 :data ..data-one..}
;               (events/validate {:line-number 2 :error ..some-error..}) => {:line-number 2 :error ..some-error..}
;               (events/validate {:line-number 3 :data ..data-three..}) => {:line-number 1 :data ..data-one.. :error ..some-error..})))

