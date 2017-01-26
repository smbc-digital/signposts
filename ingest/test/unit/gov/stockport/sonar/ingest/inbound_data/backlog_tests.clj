(ns gov.stockport.sonar.ingest.inbound-data.backlog-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]
            [gov.stockport.sonar.ingest.inbound-data.csv-reader :as csv-reader]
            [gov.stockport.sonar.ingest.inbound-data.events :as events]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]))

(fact "should provide list of feed files oldest first"
      (do
        (fsutil/configure-temp-inbound-file-system))
      (let [oldest (fsutil/spit-test-feed)
            newer (fsutil/spit-test-feed)
            newest (fsutil/spit-test-feed)]
        (backlog/waiting-feeds) => [oldest newer newest]))

(fact "should process a given feed"
      (backlog/process-file ..file..) => ..result..
      (provided
        (csv-reader/read-csv {:file ..file..}) => ..feed-with-csv-data..
        (events/csv->events ..feed-with-csv-data..) => ..feed-with-events..
        (events/events->canonical-events ..feed-with-events..) => ..feed-with-canonical-events..
        (esc/bulk-index ..feed-with-canonical-events..) => ..result..))

(facts
  "about exception handling during pipeline processing"
  (fact "csv fails to be read"
        (backlog/process-file ..file..) => ..result..
        (provided
          (csv-reader/read-csv {:file ..file..}) =throws=> (Exception. "BARF")
          (events/csv->events {:file ..file..}) => ..feed-with-events..
          (events/events->canonical-events ..feed-with-events..) => ..feed-with-canonical-events..
          (esc/bulk-index ..feed-with-canonical-events..) => ..result..)))


