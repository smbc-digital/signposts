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
      (backlog/process-file ..feed..) => ..report..
      (provided
        (csv-reader/read-csv ..feed..) => ..feed-with-csv-data..
        (events/csv->events ..feed-with-csv-data..) => ..feed-with-events..
        (esc/bulk-index-new ..feed-with-events..) => ..feed-with-index-name..
        (backlog/move-to-processed ..feed-with-index-name..) => ..report..))
