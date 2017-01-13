(ns visualise.aggregation.aggregate
  (:require [visualise.aggregation.display :as d]
            [cljs-time.core :as t]))

(defn start [event-series]
  (:timestamp (first event-series)))

(defn finish [event-series]
  (:timestamp (last event-series)))

(defn data-spread [event-series]
  (t/in-days
    (t/interval (finish event-series) (start event-series))))

; should really be doing this in a single pass for efficiency
(defn content-to [upper lower timeline]
  (filter #(t/within? lower upper (:timestamp %)) timeline))

; increasing zoom creates more buckets of a smaller size
(defn divide-into-buckets [event-series {:keys [number-of-buckets days-per-bucket]} zoom group-key]
  (let [start-date (start event-series)
        bucket-numbers (range 0 (* zoom number-of-buckets))
        bucket-size (t/seconds (- 1 (t/in-seconds (t/days (/ days-per-bucket zoom)))))]
    (map
      (fn [bucket-number]
        (let [lower (t/minus start-date (t/days (* bucket-number days-per-bucket)))
              upper (t/minus lower bucket-size)
              contents (group-by group-key (content-to upper lower event-series))]
          {:bucket-number       bucket-number
           :bucket-size-in-days bucket-size
           :event-count         (count contents)
           :upper               upper
           :lower               lower
           :contents            contents}))
      bucket-numbers)))

(defn aggregate-and-group [event-series max-buckets-on-display zoom group-key]
  (let [spread (data-spread event-series)
        display (d/display-characteristics spread max-buckets-on-display)
        buckets (divide-into-buckets event-series display zoom group-key)]
    {:spread  spread
     :display display
     :buckets buckets}))
