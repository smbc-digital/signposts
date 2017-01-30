(ns gov.stockport.sonar.ingest.fakers.faker
  (:require [gov.stockport.sonar.ingest.clock :as clock]))

(defn default-event []
  {:event-source "FAKE-SOURCE"
   :event-type   "FAKE-TYPE"
   :timestamp    (clock/now)})

(defn fake-event
  ([] (fake-event {}))
  ([overrides] (merge (default-event) overrides)))

(defn large-event
  ([] (let [keys (map #(keyword (str "KEY" %)) (range 1 31))
            entries (zipmap keys (repeatedly (fn [] (apply str (take 30 (repeatedly #(rand-int 10)))))))]
        (merge (fake-event) entries))))