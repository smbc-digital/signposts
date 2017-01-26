(ns gov.stockport.sonar.ingest.inbound-data.pipeline-stage.csv-reader-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.clock :as clock]
            [gov.stockport.sonar.ingest.inbound-data.pipeline-stage.csv-reader :as csv-reader]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [clj-time.core :as t]
            [clojure.java.io :as io])
  (:import (java.io StringReader)))

(against-background
  [(before :contents
           (do
             (fsutil/configure-temp-inbound-file-system)
             (clock/freeze! (t/date-time 2017 1 1 23 59 59 999)))
           :after
           (clock/thaw!)
           )]

  (fact "should read a real csv file"
        (let [test-file (fsutil/spit-test-feed)]
              (with-open [stream (io/reader test-file)]
                (let [{:keys [csv-data]} (csv-reader/stream->csv {:stream stream})]
                  (first csv-data) => ["event-source" "event-type" "timestamp"]
                  (rest csv-data) => [["FAKE-SOURCE" "FAKE-TYPE" "2017-01-01T23:59:59.999Z"]])))))

(fact "should be happy with any old stream"
      (:csv-data (csv-reader/stream->csv {:stream (StringReader. "heading\nvalue\n")})) => [["heading"]["value"]])
