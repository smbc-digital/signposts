(ns gov.stockport.sonar.visualise.query.handler
  (:require [gov.stockport.sonar.visualise.util.date :refer [parse-timestamp]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]))

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
                      (assoc :timespan (timespan/from-data (:result %)))
                      (merge % (people/from-data (:result %)))))))