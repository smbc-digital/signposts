(ns gov.stockport.sonar.ingest.inbound-data.csv-reader-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.clock :as clock]
            [gov.stockport.sonar.ingest.inbound-data.csv-reader :as csv-reader]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [clj-time.core :as t]))

(against-background
  [(before :contents
           (do
             (fsutil/configure-temp-inbound-file-system)
             (clock/freeze! (t/date-time 2017 1 1 23 59 59 999)))
           :after
           (clock/thaw!)
           )]

  (fact "should read a clean csv file"
        (let [test-file (fsutil/spit-test-feed)
              {:keys [file csv-data]} (csv-reader/read-csv {:file test-file})]
          file => test-file
          (first csv-data) => ["event-source" "event-type" "timestamp"]
          (rest csv-data) => [["FAKE-SOURCE" "FAKE-TYPE" "2017-01-01T23:59:59.999Z"]])))