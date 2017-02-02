(ns gov.stockport.sonar.ingest.inbound.event-buffer
  (:require [gov.stockport.sonar.ingest.util.logging :refer [log-time]]))

; empty buffer is reset and can be GC'd once flushed

; need some feed meta-data in here so that we can build the index name consistently

(defn empty-buffer [{:keys [capacity feed-hash]}]
  {:capacity capacity :qty 0 :events [] :feed-hash feed-hash})

(defn- full [{:keys [capacity qty]}]
  (>= qty capacity))

; queue / flush should return report from flusher ?

(defn create-buffer [{:keys [flush-fn] :as options}]
  (let [!buffer (atom (empty-buffer options))
        flusher (fn []
                  (log-time
                    (str "flushing events")
                    (do
                      (flush-fn @!buffer)
                      (reset! !buffer (empty-buffer options)))))]
    {:flush flusher
     :queue (fn [event]
              (let [current (swap! !buffer #(-> %
                                                (update :qty inc)
                                                (update :events (fn [events] (conj events event)))))]
                (when (full current) (flusher))
                current))}))