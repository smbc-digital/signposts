(ns visualise.aggregation.date-spread-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs-time.core :as t]
            [visualise.aggregation.date-spread :as ds]))

(def first-day-of-year (t/date-time 2017))
(def second-day-of-year (t/date-time 2017 1 2))

(def day-in-q4-2016 (t/date-time 2016 11 10))
(def day-in-q1-2017 (t/date-time 2017 2 10))

(deftest date-spread
  (testing "period of a single day"
    (let [spread (ds/date-spread first-day-of-year first-day-of-year)]
      (is (= (:from-ts spread) first-day-of-year))
      (is (= (:to-ts spread) first-day-of-year))
      (is (= (:days spread) 1))
      (is (= (:months spread) 1))
      (is (= (:quarters spread) 1))
      (is (= (:half-years spread) 1))
      (is (= (:years spread) 1))
      (is (= (:start-day first-day-of-year)))
      (is (= (:start-month first-day-of-year)))
      (is (= (:start-quarter first-day-of-year)))
      (is (= (:start-half-year first-day-of-year)))
      (is (= (:start-year first-day-of-year)))
      ))
  (testing "period of a single day"
    (let [spread (ds/date-spread first-day-of-year second-day-of-year)]
      (is (= (:from-ts spread) first-day-of-year))
      (is (= (:to-ts spread) second-day-of-year))
      (is (= (:days spread) 2))
      ))
  (testing "longer period including change of year"
    (let [spread (ds/date-spread day-in-q4-2016 day-in-q1-2017)]
      (is (= (:from-ts spread) day-in-q4-2016))
      (is (= (:to-ts spread) day-in-q1-2017))
      (is (= (:days spread) 93))
      (is (= (:months spread) 4))
      (is (= (:quarters spread) 2))
      (is (= (:half-years spread) 2))
      (is (= (:start-day day-in-q4-2016)))
      (is (= (:start-month (t/date-time 2016 11 1))))
      (is (= (:start-quarter (t/date-time 2016 10 1))))
      (is (= (:start-half-year (t/date-time 2016 7 1))))
      (is (= (:start-year (t/date-time 2016)))))))

