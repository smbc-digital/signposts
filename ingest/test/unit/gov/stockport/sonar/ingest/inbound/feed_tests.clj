(ns gov.stockport.sonar.ingest.inbound.feed-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv])
  (:import (java.io StringReader BufferedReader)))

(def file-content (fn [string] (BufferedReader. (StringReader. string))))

(against-background
  [(before :contents (swap! !config assoc :inbound-dir "resources"))]

  (facts "about feed file handling"

         (fact "no files works fine"
               (feeds/process-feeds ..feed-processor..) => irrelevant
               (provided
                 (files/list-files "resources/ready") => []))

         (fact "file is listed, processed, and moved successfully"
               (feeds/process-feeds ..feed-processor..) => [..result..]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (..feed-processor.. ..one..) => ..result..
                 (files/move-file ..one.. "resources/processed") => nil))

         (fact "exception in processing causes file to move to failed"
               (feeds/process-feeds ..feed-processor..) => []
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (..feed-processor.. ..one..) =throws=> (Exception. "BARF")
                 (files/move-file ..one.. "resources/failed") => nil
                 (log & anything) => irrelevant))

         (fact "processing that returns a failure is moved to failed"
               (feeds/process-feeds ..feed-processor..) => [{:failed ""}]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (..feed-processor.. ..one..) => {:failed ""}
                 (files/move-file ..one.. "resources/failed") => nil))

         (fact "exception in one file does not prevent other files being processed"
               (feeds/process-feeds ..feed-processor..) => irrelevant
               (provided
                 (files/list-files "resources/ready") => [..one.. ..two..]
                 (files/fname ..one..) => ..fname-one..
                 (..feed-processor.. ..one..) =throws=> (Exception. "BARF")
                 (files/fname ..two..) => ..fname-two..
                 (..feed-processor.. ..two..) => irrelevant
                 (files/move-file ..one.. "resources/failed") => nil
                 (files/move-file ..two.. "resources/processed") => nil
                 (log & anything) => irrelevant))

         (fact "exceptions are logged"
               (let [some-exception (Exception. "BARF")]
                 (feeds/process-feeds ..feed-processor..) => irrelevant
                 (provided
                   (files/list-files "resources/ready") => [..one..]
                   (files/fname ..one..) => ..fname..
                   (..feed-processor.. ..one..) =throws=> some-exception
                   (files/move-file ..one.. "resources/failed") => nil
                   (log & anything) => irrelevant :times 2))))

  (facts "about processing a single feed file"

         (defn dummy-mapper [idx line] {:line-number idx :line line})

         (fact "it parses the csv queues the results in the event buffer, and flushes once at the end"
               (feeds/feed-processor {:queue ..enqueue-fn.. :flush ..flush-fn..} ..file..) => nil
               (provided
                 (files/open-reader ..file..) =streams=> [(file-content "line1\nline2\nline3")
                                                          (file-content "line1\nline2\nline3")]
                 (csv/mapper "line1") => dummy-mapper
                 (..enqueue-fn.. {:line-number 2 :line "line2"}) => irrelevant
                 (..enqueue-fn.. {:line-number 3 :line "line3"}) => irrelevant
                 (..flush-fn..) => nil))))