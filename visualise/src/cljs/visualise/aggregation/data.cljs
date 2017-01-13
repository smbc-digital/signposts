(ns visualise.aggregation.data
  (:require [cljs-time.core :as t]
            [visualise.aggregation.aggregate :refer [aggregate-and-group]]))

(defn blips [date event-type]
  (map
    (fn [idx]
      {:timestamp  date
       :event-type event-type
       :name       (str "blip-" idx)})
    (range 1 (rand-int 5))))

(defn random-events [years]
  (let [days (* 366 years)
        now (t/now)
        days (range 0 days (+ 1 (rand-int 5)))]
    (flatten
      (map (fn [day]
             (let [date (t/minus now (t/days day))]
               (blips date (rand-nth ["A" "B" "C" "D"])))) days))))
