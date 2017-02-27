(ns visualise.query.handler
  (:require [visualise.util.date :refer [parse-timestamp]]
            [visualise.common.results.individuals :as i]))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map :_source (-> response :hits :hits))))

(defn default-handler [!state]
  (fn [response]
    (swap! !state #(-> %
                       (assoc :total (-> response :hits :total))
                       (assoc :took-millis (-> response :took))
                       (assoc :result (source-events response))))
    (swap! !state #(-> %
                       (assoc :individuals (i/individuals (:result %)))))))
