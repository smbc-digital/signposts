(ns gov.stockport.sonar.ingest.utils.quick-and-dirty
  (:require [gov.stockport.sonar.ingest.config :refer [!config]]
            [gov.stockport.sonar.ingest.utils.fake-data :as fd]
            [gov.stockport.sonar.ingest.utils.fsutil :as fs]
            [gov.stockport.sonar.ingest.client.elastic-search-client :as esc]))

(defn write-some-fake-data [amount]
  (fs/configure-temp-inbound-file-system)
  (let [data (group-by :event-source (take amount (fd/timelines)))]
    (doall (map fs/spit-test-feed (vals data)))))

(defn create-kibana-index []
  (println "setting default kibana index to events-*")
  (try
    (esc/post-json-to-es {:path    "/.kibana/index-pattern/events-*?op_type=create"
                          :payload {:title         "events-*"
                                    :timeFieldName "timestamp"}})
    (esc/post-json-to-es {:path    "/.kibana/config/5.1.1"
                          :payload {:buildNum     14566
                                    :defaultIndex "events-*"}})
    (catch Exception _)))
