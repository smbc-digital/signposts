(ns gov.stockport.sonar.visualise.data.timespan
  (:require [cljs-time.core :as t]))

(defn from-data [{:keys [result]}]
  (let [times (map :timestamp result)
        start (t/minus (apply min times) (t/months 1))
        end (t/plus (apply max times) (t/months 1))]
    {:from-date start
     :selected-from start
     :to-date   end
     :selected-to end}))