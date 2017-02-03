(ns gov.stockport.sonar.ingest.inbound.feed-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [pandect.algo.sha1 :refer [sha1]]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer])
  (:import (java.io StringReader BufferedReader)))

(def file-content (fn [string] (BufferedReader. (StringReader. string))))

(against-background
  [(before :contents (swap! !config assoc :inbound-dir "resources"))]

  (facts "about feed file handling"

         (fact "no files works fine"
               (feeds/process-feeds) => irrelevant
               (provided
                 (files/list-files "resources/ready") => []))

         (fact "file is listed, processed, and moved successfully"
               (feeds/process-feeds) => [..result..]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) => ..result..
                 (files/move-file ..one.. "resources/processed") => nil))

         (fact "exception in processing causes file to move to failed"
               (feeds/process-feeds) => []
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) =throws=> (Exception. "BARF")
                 (files/move-file ..one.. "resources/failed") => nil
                 (log & anything) => irrelevant))

         (fact "processing that returns a failure is moved to failed"
               (feeds/process-feeds) => [{:failed ""}]
               (provided
                 (files/list-files "resources/ready") => [..one..]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) => {:failed ""}
                 (files/move-file ..one.. "resources/failed") => nil))

         (fact "exception in one file does not prevent other files being processed"
               (feeds/process-feeds) => irrelevant
               (provided
                 (files/list-files "resources/ready") => [..one.. ..two..]
                 (files/fname ..one..) => ..fname-one..
                 (feeds/process-feed ..one..) =throws=> (Exception. "BARF")
                 (files/fname ..two..) => ..fname-two..
                 (feeds/process-feed ..two..) => irrelevant
                 (files/move-file ..one.. "resources/failed") => nil
                 (files/move-file ..two.. "resources/processed") => nil
                 (log & anything) => irrelevant))

         (fact "exceptions are logged"
               (let [some-exception (Exception. "BARF")]
                 (feeds/process-feeds) => irrelevant
                 (provided
                   (files/list-files "resources/ready") => [..one..]
                   (files/fname ..one..) => ..fname..
                   (feeds/process-feed ..one..) =throws=> some-exception
                   (files/move-file ..one.. "resources/failed") => nil
                   (log & anything) => irrelevant :times 2))))

  (facts "about processing a single feed file"

         (defn dummy-mapper [idx line] {:line-number idx :line line})

         (fact "hash is based on the feed file name"
               (feeds/feed-hash ..file..) => ..sha..
               (provided
                 (files/fname ..file..) => ..fname..
                 (sha1 ..fname..) => ..sha..))

         (fact "feed processing employs a buffer with a hash based on the feed file"
               (feeds/process-feed ..file..) => ..result..
               (provided
                 (feeds/feed-hash ..file..) => ..some-hash..
                 (buffer/create-buffer (as-checker #(= (:feed-hash %) ..some-hash..))) => ..buffer..
                 (feeds/process-with-buffer ..file.. ..buffer..) => ..result..))

         (fact "it parses the csv queues the results in the event buffer, and flushes once at the end"
               (feeds/process-with-buffer ..file.. {:queue ..enqueue-fn.. :flush ..flush-fn..}) => nil
               (provided
                 (files/open-reader ..file..) =streams=> [(file-content "line1\nline2\nline3")
                                                          (file-content "line1\nline2\nline3")]
                 (csv/mapper "line1") => dummy-mapper
                 (..enqueue-fn.. {:line-number 2 :line "line2"}) => irrelevant
                 (..enqueue-fn.. {:line-number 3 :line "line3"}) => irrelevant
                 (..flush-fn..) => nil))))


