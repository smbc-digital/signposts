(ns gov.stockport.sonar.ingest.elastic.event-formatter
  (:require [cheshire.core :refer [generate-string]]
            [pandect.algo.sha1 :refer [sha1]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn bulk-format [{:keys [event-source event-type] :as event}]
  (let [serialised-event (generate-string event)
        event-id (sha1 serialised-event)]
    (str
      (generate-string {:index {:_index (->kebab-case (str "events-" event-source)) :_type (->kebab-case event-type) :_id event-id}})
      "\n"
      serialised-event
      "\n")))

(defn bulk-format-events [events]
  (apply str (map (partial bulk-format) events)))

