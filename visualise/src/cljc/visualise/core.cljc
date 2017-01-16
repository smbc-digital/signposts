(ns visualise.core
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.periodic :as p]))

(defn blips [date event-type]
  (map
    (fn [idx]
      {:timestamp  date
       :event-type event-type
       :name       (str "blip-" idx)})
    (range 1 (rand-int 5))))

(defn random-events [years]
  (let [days (* 366 years)
        now (t/now)
        days (range 0 days (+ 1 (rand-int 5)))]
    (flatten
      (map (fn [day]
             (let [date (t/minus now (t/days day))]
               (blips date (rand-nth ["A" "B" "C" "D"])))) days))))

(def fd #(f/unparse (:date f/formatters) %))

(defonce events (take 10 (random-events 1)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; DATE SPREAD

(defn h1? [date]
  (< (t/month date) 7))

(defn start-half-year [date]
  (if (h1? date) (t/date-time (t/year date) 1 1) (t/date-time (t/year date) 7 1)))

(defn end-half-year [date]
  (if (h1? date) (t/date-time (t/year date) 6 30) (t/date-time (t/year date) 12 31)))

(defn quarter [date]
  (let [month (t/month date)]
    (cond
      (< month 4) 1
      (< month 7) 2
      (< month 10) 3
      :else 4)))

(defn start-quarter [date]
  (let [qtr (quarter date)]
    (t/date-time (t/year date) (+ 1 (* 3 (- qtr 1))) 1)))

(defn end-quarter [date]
  (let [qtr (quarter date)]
    (t/last-day-of-the-month (t/date-time (t/year date) (* 3 qtr)))))

(defn date-spread [from-ts to-ts]
  (let [start-year (t/date-time (t/year from-ts))
        start-month (t/date-time (t/year start-year) (t/month from-ts) 1)
        start-qtr (start-quarter from-ts)
        start-half-year (start-half-year from-ts)]
    {:from-ts         from-ts
     :to-ts           to-ts
     :days            (+ 1 (t/in-days (t/interval from-ts to-ts)))
     :start-month     start-month
     :months          (t/in-months (t/interval start-month (t/plus (t/last-day-of-the-month to-ts) (t/days 1))))
     :start-quarter   start-qtr
     :quarters        (/ (t/in-months (t/interval start-qtr (t/plus (end-quarter to-ts) (t/days 1)))) 3)
     :start-half-year start-half-year
     :half-years      (/ (t/in-months (t/interval start-half-year (t/plus (end-half-year to-ts) (t/days 1)))) 6)
     :start-year      start-year
     :years           (+ 1 (- (t/year to-ts) (t/year start-year)))}))


; AGGREGATION

(defn bucket-content [from to events]
  (let [days (t/in-days (t/interval from to))
        content (filter #(t/within? from to (:timestamp %)) events)]
    (map
      (fn [{:keys [timestamp] :as event}]
        (assoc event :position-in-bucket (/ (t/in-days (t/interval from timestamp)) days)))
      content)))

(def earlier (fn [[a _] [b _]] (t/before? a b)))

(defn aggregate-into-periods [seq qty name-fn group-fn ascending-events]
  (let [start (take qty seq)
        end (map #(t/minus % (t/seconds 1)) (rest (take qty seq)))]
    (map (fn [[idx [from to]]]
           {:bucket-number idx
            :bucket-name   (name-fn from to)
            :lower         from
            :upper         to
            :contents      (group-by group-fn (bucket-content from to ascending-events))}
           ) (zipmap (range) (sort earlier (zipmap start end))))))

(defn name-days [date]
  {:heading     (t/year date)
   :sub-heading (t/day date)})

(def fm #(f/unparse (f/formatter "MMM") %))

(defn name-months [from]
  {:heading     (t/year from)
   :sub-heading (fm from)})

(defn name-quarters [from to]
  {:heading     (t/year from)
   :sub-heading (str (fm from) " - " (fm to))})

(defn name-half-years [from to]
  {:heading     (t/year from)
   :sub-heading (str (fm from) " - " (fm to))})

(defn name-years [from _]
  {:heading (t/year from)})

(defn aggregate-into-days [{:keys [start-day days]} group-fn ascending-events]
  (aggregate-into-periods (p/periodic-seq start-day (t/days 1)) days name-days group-fn ascending-events))

(defn aggregate-into-months [{:keys [start-month months]} group-fn ascending-events]
  (aggregate-into-periods (p/periodic-seq start-month (t/months 1)) months name-months group-fn ascending-events))

(defn aggregate-into-quarters [{:keys [start-quarter quarters]} group-fn ascending-events]
  (aggregate-into-periods (p/periodic-seq start-quarter (t/months 3)) quarters name-quarters group-fn ascending-events))

(defn aggregate-into-half-years [{:keys [start-half-year half-years]} group-fn ascending-events]
  (aggregate-into-periods (p/periodic-seq start-half-year (t/months 6)) half-years name-half-years group-fn ascending-events))

(defn aggregate-into-years [{:keys [start-year years]} group-fn ascending-events]
  (aggregate-into-periods (p/periodic-seq start-year (t/years 1)) years name-years group-fn ascending-events))

(defn zoom-based-resolution [zoom {:keys [days months quarters half-years]}]
  (cond
    (< (/ days zoom) 24) aggregate-into-days
    (< (/ months zoom) 24) aggregate-into-months
    (< (/ quarters zoom) 24) aggregate-into-quarters
    (< (/ half-years zoom) 24) aggregate-into-half-years
    :else aggregate-into-years))

(defn aggregate-and-group [event-series _ zoom event-grouping-function]
  (let [ascending-events (sort-by :timestamp t/before? event-series)
        spread (date-spread (:timestamp (first ascending-events)) (:timestamp (last ascending-events)))
        aggregated-events ((zoom-based-resolution zoom spread) spread event-grouping-function ascending-events)]
    {:spread            spread
     :number-of-buckets (count aggregated-events)
     :buckets           aggregated-events}))


