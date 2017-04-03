(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.data.people :as people]))

(defn x-axis [{:keys [result]}]
  (let [times (map :timestamp result)
        start (t/minus (apply min times) (t/months 1))
        end (t/plus (apply max times) (t/months 1))]
    {:mode        "time"
     :timeFormat  "%Y/%m/%d"
     :minTickSize [1 "month"]
     :min         start
     :max         end}))

(defn label-map [{:keys [result]}]
  (zipmap (reverse (sort (into #{} (map :event-type result)))) (rest (range))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))

(defn data-points [{:keys [people] :as data}]
  (let [lm (label-map data)]
    (map
      (fn [[_ {:keys [color display data]}]]
        {:points {:show display}
         :color  (get colour-map color)
         :data   (map
                   (fn [{:keys [timestamp event-type]}]
                     [timestamp (get lm event-type)])
                   data)})
      people)))

(defn event-at [data person-index data-index]
  (let [[_ {:keys [data]}] (nth (people/by-rank data) person-index nil)]
    (nth data data-index {})))

