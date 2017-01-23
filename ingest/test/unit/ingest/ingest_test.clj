(ns ingest.ingest-test
  (:require [midje.sweet :refer :all]
            [ingest.ingest :refer [invoke]]
            [ingest.inbound-data.backlog :as backlog]))

(fact "it should process files and send to elastic search"
      (invoke) => [..report-one.. ..report-two..]
      (provided
        (backlog/waiting-feeds) => [..feed-one.. ..feed-two..]
        (backlog/process-file ..feed-one..) => ..report-one..
        (backlog/process-file ..feed-two..) => ..report-two..))
