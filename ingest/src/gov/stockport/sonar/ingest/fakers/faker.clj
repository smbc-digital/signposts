(ns gov.stockport.sonar.ingest.fakers.faker
  (:require [gov.stockport.sonar.ingest.clock :as clock]
            [gov.stockport.sonar.spec.event-spec :as es]))

(defn default-event []
  {::es/event-source "FAKE-SOURCE"
   ::es/event-type   "FAKE-TYPE"
   ::es/timestamp    (clock/now)})

(defn fake-event
  ([] (fake-event {}))
  ([overrides] (merge (default-event) overrides)))
