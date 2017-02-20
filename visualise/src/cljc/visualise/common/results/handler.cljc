(ns visualise.common.results.handler)

(defn default-handler [!state]
  (fn [results]
    (swap! !state assoc :results results)))