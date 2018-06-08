(ns gov.stockport.sonar.visualise.ui.results.flot-axes
  (:require [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.util.blur :as b]
            [clojure.string :as s]
            [gov.stockport.sonar.visualise.util.fmt-help :as fmt-help]
            [gov.stockport.sonar.visualise.util.stack :as p]))

(def fortnight-in-millis (* 1000 60 60 24 14))

(defn x-axis [{{:keys [from-date to-date]} :timespan}]
  {:mode        "time"
   :timeFormat  "%Y/%m/%d"
   :minTickSize [1 "day"]
   :min         from-date
   :max         to-date
   :color       "#142934"
   :tickColor   "#999999"
   :font-weight "bold"
   :zoomRange   [fortnight-in-millis nil]
   :panRange    [from-date to-date]})

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type  (people/all-events data))))) (rest (range))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min        0
     :max        (+ 2 (count labels))
     :zoomRange  false
     :panRange   false
     :color      "#142934"
     :tickColor "#999999"
     :position   :right
     :ticks      (map (fn [[k v]] [v (fmt-help/label(name k))]) labels)
     :labelWidth 180}))

(defn collision-key [{:keys [:timestamp :event-type]}]
  {:year       (t/year timestamp)
   :month      (t/month timestamp)
   :day        (t/day timestamp)
   :event-type event-type})

(defn- y-data-points-avoiding-collisions [data]
  (let [events (people/highlighted-events data)
        lm (label-map data)
        blurrer (b/blurrer 0.2 0.6)]
    (reduce merge {}
            (map
              (fn [[{:keys [event-type] :as k} events]]
                {k (p/new-stack (blurrer (get lm event-type) (count events)))})
              (group-by collision-key events)))))

(def highlighted? (fn [[_ pdata]] (:highlighted? pdata)))

(defn- with-dummy-series-to-ensure-axes-displayed [data]
  (concat data [{:points {:show false}}]))

(defn data-points [{:keys [people] :as data}]
  (let [ydp (y-data-points-avoiding-collisions data)
        !event-map (atom {})]
    {:event-map !event-map
     :flot-data (with-dummy-series-to-ensure-axes-displayed
                  (map-indexed
                    (fn [seriesIdx [_ {:keys [color highlighted? data]}]]
                      {:points (-> {:show highlighted?}
                                   (merge (when highlighted? {:fill 0.6 :fillColor false})))
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
                    (filter highlighted? people)))}))

(defn event-at [!event-map series-idx data-idx]
  (get-in @!event-map [series-idx data-idx]))

(defn position-for [!event-map {:keys [id]}]
  (get @!event-map id))
