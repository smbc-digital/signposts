(ns gov.stockport.sonar.ingest.elastic.event-formatter
  (:require [cheshire.core :refer [generate-string]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn bulk-format [index-hash {:keys [event-source event-type] :as event}]
  (str
    (generate-string {:index {:_index (->kebab-case (str "events-" event-source "-" index-hash)) :_type (->kebab-case event-type)}})
    "\n"
    (generate-string event)
    "\n"))

(defn bulk-format-events [index-hash events]
  (apply str (map (partial bulk-format index-hash) events)))

