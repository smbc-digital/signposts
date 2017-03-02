(ns gov.stockport.sonar.visualise.aggregation.date-spread
  (:require [cljs-time.core :as t]))

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
  (let [start-day (t/date-time (t/year from-ts) (t/month from-ts) (t/day from-ts))
        start-year (t/date-time (t/year from-ts))
        start-month (t/date-time (t/year start-year) (t/month from-ts) 1)
        start-qtr (start-quarter from-ts)
        start-half-year (start-half-year from-ts)]
    {:from-ts         from-ts
     :to-ts           to-ts
     :start-day       start-day
     :days            (+ 1 (t/in-days (t/interval from-ts to-ts)))
     :start-month     start-month
     :months          (t/in-months (t/interval start-month (t/plus (t/last-day-of-the-month to-ts) (t/days 1))))
     :start-quarter   start-qtr
     :quarters        (/ (t/in-months (t/interval start-qtr (t/plus (end-quarter to-ts) (t/days 1)))) 3)
     :start-half-year start-half-year
     :half-years      (/ (t/in-months (t/interval start-half-year (t/plus (end-half-year to-ts) (t/days 1)))) 6)
     :start-year      start-year
     :years           (+ 1 (- (t/year to-ts) (t/year start-year)))}))
