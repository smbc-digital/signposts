(ns ingest.utils.quick-and-dirty
  (:require [ingest.utils.fake-data :as fd]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojure.string :as str]
            [ingest.core :as ic]
            [ingest.config :refer [!config]]
            [ingest.faking.events.event-stream :as event-stream]
            [ingest.client.elastic-search-client :as esc]))

(defn connect [{:keys [url username password]}]
  (esr/connect url {:basic-auth [username password]}))

(def !conn (atom (connect (:elastic-search @!config))))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn write-to-index [{:keys [event-type event-source] :as event}]
  (esd/create @!conn (str "feed_" (esname event-source)) (esname event-type) event))

(defn push-some-data []
  (map write-to-index (ic/exclusion-events-in-es-format-with-event-source)))

(defn page-some-data []
  (map #(println %) (take 10 (ic/exclusion-events-in-es-format-with-event-source))))

(defn push-some-fake-data [amount]
  (esc/bulk-index (take amount (event-stream/timelines))))

;(defn push-some-fake-data [amount]
;  (esc/bulk-index (take amount (fd/timelines))))

(defn create-kibana-index []
  (println "setting default kibana index to feed_*")
  (esc/post-json-to-es {:path    "/.kibana/index-pattern/feed_*?op_type=create"
                        :payload {:title         "feed_*"
                                  :timeFieldName "timestamp"}})
  (esc/post-json-to-es {:path    "/.kibana/config/5.1.1"
                        :payload {:buildNum     14566
                                  :defaultIndex "feed_*"}}))
