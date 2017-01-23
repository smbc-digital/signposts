(ns gov.stockport.sonar.ingest.ingest-events-into-index-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.fakers.faker :refer [fake-event]]
            [gov.stockport.sonar.ingest.ingest :refer [invoke]]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]))

(defn write-test-feed []
  (fsutil/spit-test-feed [(fake-event {:event-source "INTEGRATION-TEST"})]))

(against-background
  [(before :contents
           (do
             (fsutil/configure-temp-inbound-file-system)))]

  (facts
    "about ingestion of data"
    (fact "it should load an event from the simplest feed into elastic search"
          (write-test-feed)
          (let [invocation-results (invoke)
                index-name (:index-name (first invocation-results))]
            (count invocation-results) => 1
            (get-in (esc/query (str "/" index-name "/_stats")) [:_all :total :docs :count]) => 1))))

