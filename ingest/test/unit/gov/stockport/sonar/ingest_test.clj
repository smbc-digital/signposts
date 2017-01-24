(ns gov.stockport.sonar.ingest-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest :refer [invoke]]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]))

(fact "it should process files and send to elastic search"
      (invoke) => [..report-one.. ..report-two..]
      (provided
        (backlog/waiting-feeds) => [..feed-one.. ..feed-two..]
        (backlog/process-file ..feed-one..) => ..report-one..
        (backlog/process-file ..feed-two..) => ..report-two..))
