(ns visualise.common.results.handler)

(defn source-events [response]
  (map :_source (-> response :hits :hits)))

(defn default-handler [!state]
  (fn [response]
    (swap! !state #(-> %
                       (assoc :total (-> response :hits :total))
                       (assoc :took-millis (-> response :took))
                       (assoc :result (source-events response))))))
