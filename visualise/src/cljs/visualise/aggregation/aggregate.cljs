(ns visualise.aggregation.aggregate
  (:require [visualise.aggregation.display :as d]
            [cljs-time.core :as t]
            [cljs-time.periodic :as p]))

(defn oldest [event-series]
  (:timestamp (last event-series)))

(defn newest [event-series]
  (:timestamp (first event-series)))

(defn down-to-half [date]
  (let [year (t/year date)]
    (if (< (t/month date) 7) (t/date-midnight year 1 1) (t/date-midnight year 7 1))))

(defn half-years [oldest newest]
  (count
    (take-while
      (fn [date] (t/before? date (down-to-half newest)))
      (p/periodic-seq (down-to-half oldest) (t/months 6)))))

(defn date-spread [from to]
  (let [oldest-timestamp from
        newest-timestamp to]
    {:oldest     oldest-timestamp
     :newest     newest-timestamp
     :days       (t/in-days (t/interval (t/at-midnight oldest-timestamp) (t/at-midnight newest-timestamp)))
     :months     (t/in-months (t/interval (t/last-day-of-the-month oldest-timestamp) (t/first-day-of-the-month newest-timestamp)))
     :quarters   nil
     :half-years (half-years oldest-timestamp newest-timestamp)
     :years      (+ 1 (- (t/year newest-timestamp) (t/year oldest-timestamp)))}))

(defn data-spread [event-series]
  (date-spread (oldest event-series) (newest event-series)))

; should really be doing this in a single pass for efficiency
(defn content-to [upper lower timeline]
  (filter #(t/within? lower upper (:timestamp %)) timeline))

; increasing zoom creates more buckets of a smaller size
(defn divide-into-buckets [event-series {:keys [number-of-buckets days-per-bucket]} zoom group-key]
  (let [start-date (oldest event-series)
        bucket-numbers (range 0 (* zoom number-of-buckets))
        bucket-size-in-days (/ days-per-bucket zoom)
        bucket-size (t/seconds (- 1 (t/in-seconds (t/days bucket-size-in-days))))]
    (map
      (fn [bucket-number]
        (let [lower (t/minus start-date (t/days (* bucket-number days-per-bucket)))
              upper (t/minus lower bucket-size)
              contents (group-by group-key (content-to upper lower event-series))]
          {:bucket-number       bucket-number
           :bucket-size-in-days bucket-size-in-days
           :event-count         (count contents)
           :upper               upper
           :lower               lower
           :contents            contents}))
      bucket-numbers)))

(defn aggregate-and-group [event-series max-buckets-on-display zoom group-key]
  (let [event-series-s (sort-by :timestamp t/after? event-series)
        spread (data-spread event-series-s)
        display (d/display-characteristics spread max-buckets-on-display)
        buckets (divide-into-buckets event-series-s display zoom group-key)]
    {:spread  spread
     :display display
     :buckets buckets}))
