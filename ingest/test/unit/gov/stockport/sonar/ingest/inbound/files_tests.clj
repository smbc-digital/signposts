(ns gov.stockport.sonar.ingest.inbound.files-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [gov.stockport.sonar.ingest.inbound.files :as files]))

(fact "should provide list of feed files oldest first"
      (fsutil/configure-temp-inbound-file-system)
      (let [oldest (fsutil/spit-test-feed)
            newer (fsutil/spit-test-feed)
            newest (fsutil/spit-test-feed)]
        (files/list-files (str (:inbound-dir @!config) "/ready")) => [oldest newer newest]
        (provided
          (files/mtime oldest) => 1
          (files/mtime newer) => 2
          (files/mtime newest) => 3)))