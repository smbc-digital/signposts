(ns gov.stockport.sonar.ingest.elastic.event-formatter
  (:require [cheshire.core :refer [generate-string]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn bulk-format [{:keys [event-source event-type] :as event}]
  (str
    (generate-string {:index {:_index (->kebab-case (str "events-" event-source)) :_type (->kebab-case event-type)}})
    "\n"
    (generate-string event)
    "\n"))

(defn bulk-format-events [events]
  (apply str (map bulk-format events)))

