(ns ingest.utils.fsutil
  (:require [me.raynes.fs :as fs]
            [ingest.clock :as clock]
            [ingest.config :refer [!config]]
            [ingest.fakers.fake-csv :refer [as-csv]]
            [ingest.fakers.faker :as faker]
            [clojure.string :as str]
            [clj-time.format :as f]
            [clj-time.core :as t]))

(defn configure-temp-inbound-file-system []
  (let [;fsroot (fs/file "/tmp/sonar-integration-test")]
        fsroot (fs/temp-dir "sonar-integration-test")]
    (fs/mkdir (fs/file fsroot "ready"))
    (fs/mkdir (fs/file fsroot "processed"))
    (swap! !config assoc :inbound-dir fsroot)))

(defn file-name [events]
  (str/join "-" ["events" (:event-source (first events)) (f/unparse (:date-time f/formatters) (clock/now))]))

(defn spit-test-feed
  ([] (spit-test-feed [(faker/fake-event)]))
  ([events]
   (let [test-feed-file (fs/file (:inbound-dir @!config) "ready" (file-name events))]
     (spit test-feed-file (as-csv events))
     (Thread/sleep 10)
     test-feed-file)))
