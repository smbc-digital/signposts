(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]))

(defn x-axis [events]
  (let [times (map :timestamp events)
        start (t/minus (apply min times) (t/months 1))
        end (t/plus (apply max times) (t/months 1))]
    {:mode        "time"
     :timeFormat  "%Y/%m/%d"
     :minTickSize [1 "month"]
     :min         start
     :max         end}))
