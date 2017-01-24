(ns gov.stockport.sonar.ingest.inbound-data.events-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound-data.events :as events]))

(def with-headers-only [["event-source" "event-type" "timestamp"]])

(def simplest-valid-csv-data [["event-source" "event-type" "timestamp"]
                              ["SOURCE" "TYPE" "2012-01-01T12:34:56.000Z"]])

(facts
  "about mapping csv to events"

  (fact "should map simple csv data to event"
        (let [events (events/csv->events {:csv-data simplest-valid-csv-data})]
          (:event-list events) => [{:event-source "SOURCE"
                                    :event-type   "TYPE"
                                    :timestamp    "2012-01-01T12:34:56.000Z"}]))

  (fact "should report no data for empty file"
        (:errors (events/csv->events nil)) => (contains :file-produced-no-csv-data)
        (:errors (events/csv->events {})) => (contains :file-produced-no-csv-data)
        (:errors (events/csv->events {:csv-data nil})) => (contains :file-produced-no-csv-data)
        (:errors (events/csv->events {:csv-data []})) => (contains :file-produced-no-csv-data)
        (:errors (events/csv->events {:csv-data with-headers-only})) => (contains :file-produced-no-csv-data))

  (fact "should discard rows that don't have minimum required key set for an event"
        ))


