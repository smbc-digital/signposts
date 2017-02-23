(ns visualise.common.ui.flot-data)

(defn label-map [data]
  (zipmap (reverse (sort (into #{} (map :event-type data)))) (rest (range))))

(defn collision-map [data]
  (group-by #(select-keys % [:event-type :timestamp]) data))

(defn collision-count [collision-map event-type timestamp]
  (count (get collision-map {:event-type event-type :timestamp timestamp})))

(defn y-axis [data]
  (let [labels (label-map data)]
    {:min      0
     :max      (+ 1 (count labels))
     :position :right
     :ticks    (map (fn [[k v]] [v (name k)]) labels)}))

(defn blurred [number number-of-distinct-values]
  (let [start (- number (* 0.05 (/ (- number-of-distinct-values 1) 2)))]
    (map float (take number-of-distinct-values (range start 100 0.05)))))

(defn data-points [collisions idx labels events]
  (map
    (fn [{:keys [event-type timestamp]}]
      (let [number (get labels event-type)
            distinct-values (collision-count collisions event-type timestamp)
            blurred-number (if (> distinct-values 1)
                             (nth (blurred number distinct-values) idx)
                             number)]
        [timestamp blurred-number]))
    events))

(defn series-data [series-group-fn data]
  (let [labels (label-map data)
        collisions (collision-map data)]
    (map-indexed
      (fn [idx [series events]]
        {:label series
         :data  (data-points collisions idx labels events)})
      (group-by series-group-fn data))))