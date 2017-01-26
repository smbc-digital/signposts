(ns gov.stockport.sonar.ingest.fakers.faker
  (:require [gov.stockport.sonar.ingest.clock :as clock]))

(defn default-event []
  {:event-source "FAKE-SOURCE"
   :event-type   "FAKE-TYPE"
   :timestamp    (clock/now)})

(defn fake-event
  ([] (fake-event {}))
  ([overrides] (merge (default-event) overrides)))