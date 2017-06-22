(ns gov.stockport.sonar.ingest.inbound.files-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]
            [gov.stockport.sonar.ingest.inbound.files :as files]
            [gov.stockport.sonar.ingest.fakers.faker :as faker])
  (:import (java.io File)))

(defn- expected [file]
  {:file-name (.getName ^File file)
   :file      file})

(fact "should provide set of wrapped files in order"
      (fsutil/configure-temp-inbound-file-system)
      (let [oldest (fsutil/spit-test-feed [(faker/fake-event)])
            newer (fsutil/spit-test-feed [(faker/fake-event)])
            newest (fsutil/spit-test-feed [(faker/fake-event)])]
        (files/list-files (str (:inbound-dir @!config))) => [(expected oldest)
                                                                     (expected newer)
                                                                     (expected newest)]
        (provided
          (files/mtime oldest) => 1
          (files/mtime newer) => 2
          (files/mtime newest) => 3)))

(facts "about base names and extensions"

       (fact "base name handles names in different formats"

             (files/base-name "one.csv") => "one"
             (files/base-name "one.two.csv") => "one.two"
             (files/base-name "one") => "one")

       (fact "extension handles names in different formats"

             (files/extension "one.csv") => ".csv"
             (files/extension "one.two.csv") => ".csv"
             (files/extension "one") => ""))
