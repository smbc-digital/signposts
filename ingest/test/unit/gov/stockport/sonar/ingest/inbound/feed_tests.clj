(ns gov.stockport.sonar.ingest.inbound.feed-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [pandect.algo.sha1 :refer [sha1]]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher])
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

         (fact "feed processing employs a buffer"
               (feeds/process-feed ..file..) => ..result..
               (provided
                 (buffer/create-buffer (as-checker #(= (:flush-fn %) flusher/flush-events))) => ..buffer..
                 (feeds/process-with-buffer ..file.. ..buffer..) => ..result..))

         (fact "it parses the csv queues the results in the event buffer, and flushes once at the end"
               (feeds/process-with-buffer ..file.. {:queue ..enqueue-fn.. :flush ..flush-fn..}) => nil
               (provided
                 (files/open-reader ..file..) =streams=> [(file-content "line1\nline2\nline3")
                                                          (file-content "line1\nline2\nline3")]
                 (csv/mapper "line1") => dummy-mapper
                 (..enqueue-fn.. {:line-number 2 :line "line2"}) => irrelevant
                 (..enqueue-fn.. {:line-number 3 :line "line3"}) => irrelevant
                 (..flush-fn..) => nil)))

  (fact "gets list of files to process"

        (fact "returns all files when there are no 'marker' files present"

              (feeds/get-csvs ..dir-name..) => [{:file-name "some-file.csv"} {:file-name "some-other-file.csv"}]
              (provided
                (files/list-wrapped-files ..dir-name..) => [{:file-name "some-file.csv"} {:file-name "some-other-file.csv"}]))

        (fact "exclude files if a 'marker' file is present e.g. .done"
              (feeds/get-csvs ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                {:file-name "another-new-file.csv" :file ..another-file..}]
              (provided
                (files/list-wrapped-files ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                            {:file-name "some-other-file.done"}
                                                            {:file-name "another-new-file.csv" :file ..another-file..}
                                                            {:file-name "some-other-file.csv"}]))

        (fact "exclude file if it is a .csv file"
              (feeds/get-csvs ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                {:file-name "another-new-file.csv" :file ..another-file..}]


        (provided
          (files/list-wrapped-files ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                      {:file-name "some-other-file.done"}
                                                      {:file-name "some-text-file.txt"}
                                                      {:file-name "another-new-file.csv" :file ..another-file..}
                                                      {:file-name "some-other-file.csv"}]))

        ))


