(ns gov.stockport.sonar.ingest.inbound.files-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.fakers.faker :as faker])
  (:import (java.io File)))

; TO REMOVE
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

(defn- expected [file]
  {:file-name (.getName ^File file)
   :file      file})

(fact "should provide set of wrapped files in order"
      (fsutil/configure-simple-temp-inbound-file-system)
      (let [oldest (fsutil/spit-test-feed [(faker/fake-event)] "")
            newer (fsutil/spit-test-feed [(faker/fake-event)] "")
            newest (fsutil/spit-test-feed [(faker/fake-event)] "")]
        (files/list-wrapped-files (str (:inbound-dir @!config))) => [(expected oldest)
                                                                     (expected newer)
                                                                     (expected newest)]
        (provided
          (files/mtime oldest) => 1
          (files/mtime newer) => 2
          (files/mtime newest) => 3)))