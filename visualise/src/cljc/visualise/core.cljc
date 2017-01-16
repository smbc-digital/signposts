(ns visualise.core
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.periodic :as p]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.periodic :as p]
            [clj-time.core :as t]
            [clj-time.core :as t]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.core :as t]
            [clj-time.core :as t]
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
  (filter #(t/within? from to (:timestamp %)) events))

(defn aggregate-into-periods [seq qty ascending-events]
  (let [start (take qty seq)
        end (map #(t/minus % (t/seconds 1)) (rest (take qty seq)))]
    (map (fn [[idx [from to]]]
           {:bucket-number idx
            :from          from
            :to            to
            :contents      (bucket-content from to ascending-events)}
           ) (zipmap (range) (zipmap start end)))))

(defn aggregate-into-months [{:keys [start-month months]} ascending-events]
  (aggregate-into-periods (p/periodic-seq start-month (t/months 1)) months ascending-events))

(defn aggregate-into-quarters [{:keys [start-quarter quarters]} ascending-events]
  (aggregate-into-periods (p/periodic-seq start-quarter (t/months 3)) quarters ascending-events))

(defn aggregate-into-half-years [{:keys [start-half-year half-years]} ascending-events]
  (aggregate-into-periods (p/periodic-seq start-half-year (t/months 6)) half-years ascending-events))

(defn aggregate-into-years [{:keys [start-year years]} ascending-events]
  (aggregate-into-periods (p/periodic-seq start-year (t/years 1)) years ascending-events))

(defn aggregate-and-group [event-series _ _ _]
  (let [ascending-events (sort-by :timestamp t/before? event-series)
        spread (date-spread (:timestamp (first ascending-events)) (:timestamp (last ascending-events)))]
    {:spread spread
     :display {}
     :buckets (aggregate-into-half-years spread ascending-events)}
    ))



