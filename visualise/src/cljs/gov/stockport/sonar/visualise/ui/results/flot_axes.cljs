(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.util.blur :as b]
            [gov.stockport.sonar.visualise.util.popper :as p]))

(defn x-axis [{{:keys [selected-from selected-to]} :timespan}]
  {:mode        "time"
   :timeFormat  "%Y/%m/%d"
   :minTickSize [1 "day"]
   :min         selected-from
   :max         selected-to})

(defn selector-x-axis [{{:keys [from-date to-date]} :timespan}]
  {:mode        "time"
   :timeFormat  "%Y/%m/%d"
   :minTickSize [1 "month"]
   :min         from-date
   :max         to-date})

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type (people/all-events data))))) (rest (range))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))

(defn collision-key [{:keys [:timestamp :event-type]}]
  {:year       (t/year timestamp)
   :month      (t/month timestamp)
   :day        (t/day timestamp)
   :event-type event-type})

(defn- y-data-points-avoiding-collisions [data]
  (let [events (people/all-events data)
        lm (label-map data)
        blurrer (b/blurrer 0.1)]
    (reduce merge {}
            (map
              (fn [[{:keys [event-type] :as k} events]]
                (when (= (count events) 0) (println event-type))
                {k (p/poppable (blurrer (get lm event-type) (count events)))})
              (group-by collision-key events)))))

(defn data-points [{:keys [people] :as data}]
  (let [ydp (y-data-points-avoiding-collisions data)]
    (map
      (fn [[_ {:keys [color displayed? data]}]]
        {:points {:show displayed?}
         :color  (get colour-map color)
         :data   (map
                   (fn [{:keys [timestamp] :as event}]
                     (let [ekey (collision-key event)
                           next-val-fn (get ydp ekey)]
                       (when (nil? next-val-fn) (println ekey))
                       [timestamp (next-val-fn)]))
                   data)})
      people)))

(defn event-at [data person-index data-index]
  (let [[_ {:keys [data]}] (nth (people/by-rank data) person-index nil)]
    (nth data data-index {})))