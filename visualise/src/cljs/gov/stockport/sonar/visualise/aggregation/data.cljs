(ns gov.stockport.sonar.visualise.aggregation.data
  (:require [cljs-time.core :as t]))

(defn blips [date event-source event-type]
  (map
    (fn [idx]
      {:timestamp    date
       :event-source event-source
       :event-type   event-type
       :name         (str "blip-" idx)})
    (range 1 (rand-int 5))))

(defn random-events [years]
  (let [days (* 366 years)
        now (t/now)
        days (range 0 days (+ 1 (rand-int 5)))]
    (flatten
      (map (fn [day]
             (let [date (t/minus now (t/days day))]
               (blips date (rand-nth ["GMP" "SCHOOLS"]) (rand-nth ["A" "B" "C" "D"])))) days))))
