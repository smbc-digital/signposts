(ns gov.stockport.sonar.visualise.query.handler
  (:require [gov.stockport.sonar.visualise.util.date :refer [parse-timestamp]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.data.timespan :as timespan]))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map
         (fn [{:keys [_score _source _id]}]
           (assoc _source :score _score :id _id))
         (-> response :hits :hits))))

(defn default-handler [!data]
  (fn [response]
    (let [results (source-events response)]
      (swap! !data #(-> %
                        (assoc :search-uuid (random-uuid))
                        (assoc :total (-> response :hits :total))
                        (assoc :took-millis (-> response :took))
                        (assoc :result results)
                        (people/from-data))))))
