(ns gov.stockport.sonar.ingest-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest :refer [invoke]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]
            [gov.stockport.sonar.ingest.inbound-data.pipeline :as pipeline]))

(fact "it should process files and send to elastic search"
      (invoke) => [..report-one.. ..report-two..]
      (provided
        (backlog/list-waiting-csv-files) => [..feed-one.. ..feed-two..]
        (pipeline/process-event-data ..feed-one..) => ..report-one..
        (pipeline/process-event-data ..feed-two..) => ..report-two..))
