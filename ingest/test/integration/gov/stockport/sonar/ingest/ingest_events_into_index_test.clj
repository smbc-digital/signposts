(ns gov.stockport.sonar.ingest.ingest-events-into-index-test
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.fakers.faker :refer [fake-event]]
            [gov.stockport.sonar.ingest :refer [invoke]]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]
            [gov.stockport.sonar.ingest.utils.fsutil :as fsutil]))

(defn write-test-feed []
  (fsutil/spit-test-feed [(fake-event {:event-source "INTEGRATION-TEST"})]))

(against-background
  [(before :facts
           (do
             (fsutil/configure-temp-inbound-file-system)
             (esc/try-delete "/events-integration-test-*")))]

  (fact "it should load an event from the simplest feed into elastic search"
        (write-test-feed)
        (let [invocation-results (invoke)
              index-name (:index-name (first invocation-results))]
          (count invocation-results) => 1
          (get-in (esc/query (str "/" index-name "/_stats")) [:_all :total :docs :count]) => 1))

  ;(fact "it should not re-process the file if invoked twice"
  ;      (write-test-feed)
  ;      (let [invocation-results (invoke)
  ;            second-invocation-results (invoke)
  ;            index-name (:index-name (first invocation-results))]
  ;        (count invocation-results) => 1
  ;        (count second-invocation-results) => 0
  ;        (get-in (esc/query (str "/" index-name "/_stats")) [:_all :total :docs :count]) => 1))
  )

