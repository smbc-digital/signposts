(ns gov.stockport.sonar.ingest.utils.fsutil
  (:require [me.raynes.fs :as fs]
            [gov.stockport.sonar.ingest.clock :as clock]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.fakers.fake-csv :refer [as-csv]]
            [gov.stockport.sonar.ingest.fakers.faker :as faker]
            [clojure.string :as str]))

(defn configure-temp-inbound-file-system []
  (let [;fsroot (fs/file "/tmp/sonar-integration-test")]
        fsroot (fs/temp-dir "sonar-fake-data-test")]
    (fs/mkdir (fs/file fsroot "ready"))
    (fs/mkdir (fs/file fsroot "processed"))
    (fs/mkdir (fs/file fsroot "failed"))
    (swap! !config assoc :inbound-dir fsroot)))

(defn file-name [events]
  (str (str/join "-" ["events" (:event-source (first events)) (clock/now-millis)]) ".csv"))

(defn spit-test-feed
  ([] (spit-test-feed [(faker/fake-event)]))
  ([events]
   (let [test-feed-file (fs/file (:inbound-dir @!config) "ready" (file-name events))]
     (spit test-feed-file (as-csv events))
     (Thread/sleep 10)
     test-feed-file)))
