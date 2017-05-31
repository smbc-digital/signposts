(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.util.blur :as b]
            [gov.stockport.sonar.visualise.util.stack :as p]))

(def fortnight-in-millis (* 1000 60 60 24 14))

(defn x-axis [{{:keys [from-date to-date]} :timespan}]
  {:mode        "time"
   :timeFormat  "%Y/%m/%d"
   :minTickSize [1 "day"]
   :min         from-date
   :max         to-date
   :zoomRange   [fortnight-in-millis nil]
   :panRange    [from-date to-date]})

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type (people/all-events data))))) (rest (range))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min        0
     :max        (+ 2 (count labels))
     :zoomRange  false
     :panRange   false
     :position   :right
     :ticks      (map (fn [[k v]] [v (name k)]) labels)
     :labelWidth 50
     }))

(defn collision-key [{:keys [:timestamp :event-type]}]
  {:year       (t/year timestamp)
   :month      (t/month timestamp)
   :day        (t/day timestamp)
   :event-type event-type})

(defn- y-data-points-avoiding-collisions [data]
  (let [events (people/all-events data)
        lm (label-map data)
        blurrer (b/blurrer 0.1 0.4)]
    (reduce merge {}
            (map
              (fn [[{:keys [event-type] :as k} events]]
                {k (p/new-stack (blurrer (get lm event-type) (count events)))})
              (group-by collision-key events)))))

(def by-highlighted? (fn [[_ pdata]] ((juxt :highlighted? :rank) pdata)))

(defn data-points [{:keys [people show-only-highlighted?] :as data}]
  (let [ydp (y-data-points-avoiding-collisions data)
        !event-map (atom {})]
    {:event-map !event-map
     :flot-data (map-indexed
                  (fn [seriesIdx [_ {:keys [color highlighted? data]}]]
                    {:points (-> {:show (or (not show-only-highlighted?) highlighted?)}
                                 (merge (when highlighted? {:fill 0.8 :fillColor false})))
                     :color  (get colour-map (or color :black))
                     :data   (map-indexed
                               (fn [dataIdx {:keys [timestamp] :as event}]
                                 (swap! !event-map
                                        #(-> %
                                             (assoc-in [seriesIdx dataIdx] event)
                                             (assoc (:id event) {:seriesIndex seriesIdx :dataIndex dataIdx})))
                                 (let [ekey (collision-key event)
                                       stack (get ydp ekey)
                                       pop (:pop stack)]
                                   [timestamp (pop)]))
                               data)})
                  (sort-by by-highlighted? people))}))

(defn event-at [!event-map series-idx data-idx]
  (get-in @!event-map [series-idx data-idx]))

(defn position-for [!event-map {:keys [id]}]
  (get @!event-map id))
