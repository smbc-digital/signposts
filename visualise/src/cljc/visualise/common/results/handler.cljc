(ns visualise.common.results.handler
  #?(:cljs (
             :require
             [visualise.util.date :refer [parse-timestamp]]))
  #?@(:clj [
            (
              :require
              [visualise.common.util.foreign :refer [parse-timestamp]])]))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map :_source (-> response :hits :hits))))


(defn default-handler [!state]
  (fn [response]
    (swap! !state #(-> %
                       (assoc :total (-> response :hits :total))
                       (assoc :took-millis (-> response :took))
                       (assoc :result (source-events response))))))