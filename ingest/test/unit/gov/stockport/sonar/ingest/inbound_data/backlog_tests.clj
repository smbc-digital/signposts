(ns gov.stockport.sonar.ingest.inbound-data.backlog-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [gov.stockport.sonar.ingest.inbound-data.backlog :as backlog]))

(fact "should provide list of feed files oldest first"
      (fsutil/configure-temp-inbound-file-system)
      (let [oldest (fsutil/spit-test-feed)
            newer (fsutil/spit-test-feed)
            newest (fsutil/spit-test-feed)]
        (map :file (backlog/list-waiting-csv-files)) => [oldest newer newest]))