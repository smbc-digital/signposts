(ns gov.stockport.sonar.visualise.query.handler
  (:require [gov.stockport.sonar.visualise.util.date :refer [parse-timestamp]]
            [gov.stockport.sonar.visualise.common.results.individuals :as i]))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map
         (fn [{:keys [_score _source]}]
           (assoc _source :score _score))
         (-> response :hits :hits))))

(defn default-handler [!data]
  (fn [response]
    (swap! !data #(-> %
                      (assoc :total (-> response :hits :total))
                      (assoc :took-millis (-> response :took))
                      (assoc :result (source-events response))
                      (dissoc :point :selected-event)))
    (swap! !data #(-> %
                      (assoc :individuals (i/individuals (:result %)))))))
