(ns visualise.common.ui.flot-data
  (:require [visualise.common.util.blur :as b]
            [visualise.common.results.individuals :as i]))

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type data)))) (rest (range))))

(defn collision-map [data]
  (group-by #(select-keys % [:event-type :timestamp]) data))

(defn collision-count [collision-map event-type timestamp]
  (count (get collision-map {:event-type event-type :timestamp timestamp})))

(defn y-axis-label-map [event-types]
  (reduce merge {} (map-indexed (fn [idx et] {et (+ idx 1)}) (reverse (sort event-types)))))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))


(defn data-points [collisions idx labels events]
  (map
    (fn [{:keys [event-type timestamp]}]
      (let [number (get labels event-type)
            distinct-values (collision-count collisions event-type timestamp)
            blurred-number (if (> distinct-values 1)
                             (nth (b/blurred number distinct-values) idx)
                             number)]
        [timestamp blurred-number]))
    events))

(defn individual-data [individual-events]
  (map
    (fn [{:keys [timestamp event-type]}]
      [timestamp event-type])
    individual-events))

(defn series-meta [data]
  (let [individuals (i/individuals data)
        individual->data (i/group-by-individual data)]
    (map
      (fn [{:keys [ikey] :as individual}]
        {:individual individual
         :data       (individual-data (get individual->data ikey))})
      individuals)))

(def colors [:red :blue :green :yellow :orange :purple])

(defn flot-series-data [label-map meta-data]
  (map
    (fn [{{:keys [idx]} :individual data :data}]
      {:color (or (get colors idx) :black)
       :data  (map (fn [[k v]] [k (get label-map v)]) data)})
    (sort-by #(get-in % [:individual :idx]) meta-data)))