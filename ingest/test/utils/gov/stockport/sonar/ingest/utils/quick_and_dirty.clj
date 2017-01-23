(ns gov.stockport.sonar.ingest.utils.quick-and-dirty
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.faking.events.event-stream :as event-stream]
            [gov.stockport.sonar.ingest.utils.fake-data :as fd]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]))

; use for the supposedly more realistic data
;(defn push-some-fake-data [amount]
;  (esc/bulk-index (take amount (event-stream/timelines))))

; as per the original demo fake data
(defn push-some-fake-data [amount]
  (esc/bulk-index (take amount (fd/timelines))))

(defn create-kibana-index []
  (println "setting default kibana index to feed_*")
  (esc/post-json-to-es {:path    "/.kibana/index-pattern/feed_*?op_type=create"
                        :payload {:title         "feed_*"
                                  :timeFieldName "timestamp"}})
  (esc/post-json-to-es {:path    "/.kibana/config/5.1.1"
                        :payload {:buildNum     14566
                                  :defaultIndex "feed_*"}}))
