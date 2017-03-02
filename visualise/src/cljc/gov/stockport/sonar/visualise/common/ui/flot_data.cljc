(ns gov.stockport.sonar.visualise.common.ui.flot-data
  (:require [gov.stockport.sonar.visualise.common.util.blur :as b]
            [gov.stockport.sonar.visualise.common.results.individuals :as i]
            [gov.stockport.sonar.visualise.common.ui.colors :as c]))

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type data)))) (rest (range))))

(defn collision-map [data]
  (reduce
    merge {}
    (filter (fn [[_ v]] (> v 1))
            (map
              (fn [[k v]]
                [k (count v)])
              (group-by #(select-keys % [:event-type :timestamp]) data)))))


(defn y-axis-label-map [event-types]
  (reduce merge {} (map-indexed (fn [idx et] {et (+ idx 1)}) (reverse (sort event-types)))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))

(defn individual-data [individual-events]
  (map
    (fn [{:keys [timestamp event-type] :as event}]
      [timestamp event-type event])
    individual-events))

(defn series-meta [data]
  (let [individuals (i/individuals data)
        individual->data (i/group-by-individual data)]
    (map
      (fn [{:keys [ikey] :as individual}]
        {:individual individual
         :data       (individual-data (get individual->data ikey))})
      individuals)))

(defn collision-count [collision-map event-type timestamp]
  (or (get collision-map {:event-type event-type :timestamp timestamp}) 1))

(defn data-points [collisions idx labels events]
  (map
    (fn [[timestamp event-type]]
      (let [number (get labels event-type)
            distinct-values (collision-count collisions event-type timestamp)
            blurred-number (if (> distinct-values 1)
                             (nth (b/blurred number distinct-values) idx)
                             number)]
        [timestamp blurred-number]))
    events))

(defn flot-series-data [label-map collisions meta-data]
  (map
    (fn [{{:keys [idx color]} :individual data :data}]
      {:color (get c/color-map color)
       :data  (data-points collisions idx label-map data)
       })
    (sort-by #(get-in % [:individual :idx]) meta-data)))