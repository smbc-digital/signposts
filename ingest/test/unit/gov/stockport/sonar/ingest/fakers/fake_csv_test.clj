(ns gov.stockport.sonar.ingest.fakers.fake-csv-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.fakers.fake-csv :as fcsv]
            [clj-time.core :as t]))

(facts
  "about csv production"
  (fact "when there are no events"
        (fcsv/as-csv []) => nil)
  (fact "simple csv is generated"
        (fcsv/as-csv [{:event-source "SOURCE" :event-type "TYPE"}])
        => "event-source,event-type\nSOURCE,TYPE\n")
  (fact "integer values are handled"
        (fcsv/as-csv [{:qty 5}])
        => "qty\n5\n")
  (fact "floats are handled"
        (fcsv/as-csv [{:qty 5.99}])
        => "qty\n5.99\n")
  (fact "multiple rows are handled"
        (fcsv/as-csv [{:qty 5.99} {:qty 3.9}])
        => "qty\n5.99\n3.9\n")
  (fact "quoting is provided where necessary"
        (fcsv/as-csv [{:event-source "SOURCE,WITH,COMMA" :event-type "TYPE"}])
        => "event-source,event-type\n\"SOURCE,WITH,COMMA\",TYPE\n")
  (fact "and date objects are handled"
        (fcsv/as-csv [{:event-source "SOURCE,WITH,COMMA" :event-type "TYPE" :timestamp
                                     (t/date-time 2017 1 1 13 45 15 299)}])
        => "event-source,event-type,timestamp\n\"SOURCE,WITH,COMMA\",TYPE,2017-01-01T13:45:15.299Z\n"))

