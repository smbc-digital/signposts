(ns gov.stockport.sonar.ingest.inbound.feed-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.inbound.feeds :as feeds]
            [gov.stockport.sonar.ingest.helper.logging :refer [log]]
            [gov.stockport.sonar.ingest.inbound.csv :as csv]
            [pandect.algo.sha1 :refer [sha1]]
            [gov.stockport.sonar.ingest.inbound.event-buffer :as buffer]
            [gov.stockport.sonar.ingest.inbound.flusher :as flusher]
            [pandect.algo.md5 :as md5])
  (:import (java.io StringReader BufferedReader)))

(def file-content (fn [string] (BufferedReader. (StringReader. string))))

(against-background
  [(before :contents (swap! !config assoc :inbound-dir "resources"))]

  (facts "about feed file handling"

         (fact "no files works fine"
               (feeds/process-feeds) => irrelevant
               (provided
                 (feeds/get-csvs "resources") => []))

         (fact "file is listed, processed, and moved successfully"
               (feeds/process-feeds) => [..result..]
               (provided
                 (feeds/get-csvs "resources") => [{:file ..one.. :file-name ..one-name..}]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) => ..result..
                 (files/write-done-file ..one-name..) => nil
                 (feeds/should-process-feed-file irrelevant) => true))

         (fact "exception in processing causes file to move to failed"
               (feeds/process-feeds) => []
               (provided
                 (feeds/get-csvs "resources") => [{:file ..one.. :file-name ..one-name..}]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) =throws=> (Exception. "BARF")
                 (files/write-failed-file ..one-name..) => nil
                 (feeds/should-process-feed-file irrelevant) => true
                 (log & anything) => irrelevant))

         (fact "processing that returns a failure is moved to failed"
               (feeds/process-feeds) => [{:failed ""}]
               (provided
                 (feeds/get-csvs "resources") => [{:file ..one.. :file-name ..one-name..}]
                 (files/fname ..one..) => ..fname..
                 (feeds/process-feed ..one..) => {:failed ""}
                 (files/write-failed-file ..one-name..) => nil
                 (feeds/should-process-feed-file irrelevant) => true))

         (fact "exception in one file does not prevent other files being processed"
               (feeds/process-feeds) => irrelevant
               (provided
                 (feeds/get-csvs "resources") => [{:file ..one.. :file-name ..one-name..} {:file ..two.. :file-name ..two-name..}]
                 (files/fname ..one..) => ..fname-one..
                 (feeds/process-feed ..one..) =throws=> (Exception. "BARF")
                 (files/fname ..two..) => ..fname-two..
                 (feeds/process-feed ..two..) => irrelevant
                 (files/write-failed-file ..one-name..) => nil
                 (files/write-done-file ..two-name..) => nil
                 (feeds/should-process-feed-file irrelevant) => true
                 (log & anything) => irrelevant))

         (fact "exceptions are logged"
               (let [some-exception (Exception. "BARF")]
                 (feeds/process-feeds) => irrelevant
                 (provided
                   (feeds/get-csvs "resources") => [{:file ..one.. :file-name ..one-name..}]
                   (files/fname ..one..) => ..fname..
                   (feeds/process-feed ..one..) =throws=> some-exception
                   (files/write-failed-file ..one-name..) => nil
                   (feeds/should-process-feed-file irrelevant) => true
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
                (files/list-files ..dir-name..) => [{:file-name "some-file.csv"} {:file-name "some-other-file.csv"}]))

        (fact "exclude file if it is a .csv file"
              (feeds/get-csvs ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                {:file-name "another-new-file.csv" :file ..another-file..}
                                                {:file-name "some-other-file.csv" :file ..some-other-file..}]
              (provided
                (files/list-files ..dir-name..) => [{:file-name "some-file.csv" :file ..some-file..}
                                                            {:file-name "some-other-file.done"}
                                                            {:file-name "some-text-file.txt"}
                                                            {:file-name "another-new-file.csv" :file ..another-file..}
                                                            {:file-name "some-other-file.csv" :file ..some-other-file..}]))))


(facts "about deciding whether to process the csv file"

       (fact "should write the md5 of the contents of the csv file into the done file"
             (files/write-done-file "one.csv") => irrelevant
             (provided
               (md5/md5-file "resources/one.csv") => ..md5-of-contents..
               (files/write-content-to-file "one.done" ..md5-of-contents..) => irrelevant))

       (fact "should not process the csv file if the done file exists and the md5 matches"
             (feeds/should-process-feed-file "one.csv") => false
             (provided
               (files/exists? "resources/one.failed") => false
               (files/exists? "resources/one.done") => true
               (md5/md5-file "resources/one.csv") => ..md5-of-contents..
               (slurp "resources/one.done") => ..md5-of-contents..))

       (fact "should process the csv file if the done file does not exist"
             (feeds/should-process-feed-file "one.csv") => true
             (provided
               (files/exists? "resources/one.done") => false
               (files/exists? "resources/one.failed") => false))

       (fact "should process the csv file if the md5 doesn't match"
             (feeds/should-process-feed-file "one.csv") => true
             (provided
               (files/exists? "resources/one.done") => true
               (files/exists? "resources/one.failed") => false
               (md5/md5-file "resources/one.csv") => "fadsfadsfasdfsd"
               (slurp "resources/one.done") => "not matching md5"))

       (fact "should not process the csv file if there is a .failed file"
             (feeds/should-process-feed-file "one.csv") => false
             (provided
               (files/exists? "resources/one.failed") => true))
       )
;; If file.done is not there, create it and process the file
;; If file.done is there and the MD5 in it doesn't match the MD5 of the file contents, then the file
;;  has changed since the last ingest, so process the whole file again
;;  (it is ok to process the whole file again, since using the row's SHA as the ID means that we won't have duplicates)
;; If file.done exists and the MD5 of file contents matches MD5 recorded in file.done, then ignore the file
;; the file contents haven't changed since the last time it was processed.
