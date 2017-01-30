(ns gov.stockport.sonar.ingest.inbound-data.pipeline-stage.event-parsing-test
  (:require [midje.sweet :refer :all]
            [midje.checking.core :as checking]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.inbound-data.pipeline-stage.event-parsing :as events]
            [clojure.spec :as s]))

(defn contains-no-errors? [{rejects :rejected-events}]
  (let [reject-count (count rejects)]
    (or (= 0 reject-count)
        (checking/as-data-laden-falsehood {:notes (map #(s/explain-str ::es/event %) rejects)}))))


(def with-headers-only [["event-source" "event-type" "timestamp"]])

(def simplest-valid-csv-data [["event-source" "event-type" "timestamp"]
                              ["SOURCE" "TYPE" "2012-01-01T12:34:56.000Z"]])

(def valid-csv-data-with-whitespace [["event-source" "event-type" "timestamp"]
                                     ["  SOURCE " "  TYPE " "  2012-01-01T12:34:56.000Z "]])

(def single-failing-record [["incorrect-heading"]
                            ["irrelevant value"]])

(facts
  "about mapping csv to events"

  (fact "should map simple csv data to event"
        (let [events (events/->events {:csv simplest-valid-csv-data})]
          events => contains-no-errors?
          (:valid-events events) => [{::es/event-source "SOURCE"
                                      ::es/event-type   "TYPE"
                                      ::es/timestamp    "2012-01-01T12:34:56.000Z"}]))

  (fact "should map csv data with whitespace to event"
        (let [events (events/->events {:csv valid-csv-data-with-whitespace})]
          events => contains-no-errors?
          (:valid-events events) => [{::es/event-source "SOURCE"
                                      ::es/event-type   "TYPE"
                                      ::es/timestamp    "2012-01-01T12:34:56.000Z"}]))

  (fact "should report no data for empty file"
        (count (:valid-events (events/->events nil))) => 0
        (count (:valid-events (events/->events {}))) => 0
        (count (:valid-events (events/->events {:csv nil}))) => 0
        (count (:valid-events (events/->events {:csv []}))) => 0
        (count (:valid-events (events/->events {:csv with-headers-only}))) => 0)

  (fact "should discard invalid events"
        (count (:rejected-events (events/->events {:csv single-failing-record}))) => 1))