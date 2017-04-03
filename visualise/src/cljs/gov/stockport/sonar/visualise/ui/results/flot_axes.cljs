(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.util.blur :as b]
            [gov.stockport.sonar.visualise.util.popper :as p]))

(defn x-axis [{{:keys [from-date to-date]} :timespan}]
  {:mode        "time"
   :timeFormat  "%Y/%m/%d"
   :minTickSize [1 "month"]
   :min         from-date
   :max         to-date})

(defn label-map [{:keys [result]}]
  (zipmap (reverse (sort (into #{} (map :event-type result)))) (rest (range))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))

(defn- y-data-points-avoiding-collisions [{:keys [result] :as data}]
  (let [lm (label-map data)
        blurrer (b/blurrer 0.1)]
    (reduce merge {}
            (map
              (fn [[{:keys [event-type] :as k} events]]
                {k (p/poppable (blurrer (get lm event-type) (count events)))})
              (group-by #(select-keys % [:timestamp :event-type]) result)))))

(defn data-points [{:keys [people] :as data}]
  (let [ydp (y-data-points-avoiding-collisions data)]
    (map
      (fn [[_ {:keys [color display data]}]]
        {:points {:show display}
         :color  (get colour-map color)
         :data   (map
                   (fn [{:keys [timestamp] :as event}]
                     (let [next-val-fn (get ydp (select-keys event [:timestamp :event-type]))]
                       [timestamp (next-val-fn)]))
                   data)})
      people)))

(defn event-at [data person-index data-index]
  (let [[_ {:keys [data]}] (nth (people/by-rank data) person-index nil)]
    (nth data data-index {})))