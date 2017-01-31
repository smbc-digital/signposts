(ns gov.stockport.sonar.ingest.inbound.feed-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [clojure-csv.core :as csv]))

(against-background
  [(before :contents (swap! !config assoc :inbound-dir "resources"))]

  (facts "about csv processing"

         (fact "stream is opened an closed around csv processing"
               (feeds/apply-csv-processing ..file.. ..csv-processor..) => ..result..
               (provided
                 (files/open-reader ..file..) => ..rdr..
                 (csv/parse-csv ..rdr..) => ..csv-stream..
                 (files/name ..file..) => ..file-name..
                 (feeds/fhash ..file..) => ..feed-hash..
                 (..csv-processor.. {:name      ..file-name..
                                     :feed-hash ..feed-hash..
                                     :csv       ..csv-stream..}) => ..result..
                 (files/close-reader ..rdr..) => nil))

         (fact "exceptions are propagated, but stream is still closed"
               (feeds/apply-csv-processing ..file.. ..csv-processor..) => (throws Exception "BARF")
               (provided
                 (files/open-reader ..file..) => ..rdr..
                 (files/name ..file..) => ..file-name..
                 (feeds/fhash ..file..) => ..feed-hash..
                 (csv/parse-csv ..rdr..) => ..csv-stream..
                 (..csv-processor.. anything) =throws=> (Exception. "BARF")
                 (files/close-reader ..rdr..) => nil)))


  (facts "about feed file handling"

         (fact "no files works fine"
               (feeds/process-feeds ..csv-processor..) => irrelevant
               (provided
                 (files/list-files "resources/ready") => []))

         (fact "file is listed, processed, and moved successfully"
               (feeds/process-feeds ..csv-processor..) => [..result..]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (feeds/apply-csv-processing ..one.. ..csv-processor..) => ..result..
                 (files/move-file ..one.. "resources/processed") => nil))

         (fact "exception in processing causes file to move to failed"
               (feeds/process-feeds ..csv-processor..) => []
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (feeds/apply-csv-processing ..one.. ..csv-processor..) =throws=> (Exception. "BARF")
                 (files/move-file ..one.. "resources/failed") => nil
                 (log anything) => irrelevant))

         (fact "processing that returns a failure is moved to failed"
               (feeds/process-feeds ..csv-processor..) => [{:failed ""}]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (feeds/apply-csv-processing ..one.. ..csv-processor..) => {:failed ""}
                 (files/move-file ..one.. "resources/failed") => nil))

         (fact "exception in one file does not prevent other files being processed"
               (feeds/process-feeds ..csv-processor..) => irrelevant
               (provided
                 (files/list-files "resources/ready") => [..one.. ..two..]
                 (feeds/apply-csv-processing ..one.. ..csv-processor..) =throws=> (Exception. "BARF")
                 (feeds/apply-csv-processing ..two.. ..csv-processor..) => irrelevant
                 (files/move-file ..one.. "resources/failed") => nil
                 (files/move-file ..two.. "resources/processed") => nil
                 (log anything) => irrelevant))

         (fact "exceptions are logged"
               (let [some-exception (Exception. "BARF")]
                 (feeds/process-feeds ..csv-processor..) => irrelevant
                 (provided
                   (files/list-files "resources/ready") => [..one..]
                   (feeds/apply-csv-processing ..one.. ..csv-processor..) =throws=> some-exception
                   (files/move-file ..one.. "resources/failed") => nil
                   (log some-exception) => irrelevant)))))