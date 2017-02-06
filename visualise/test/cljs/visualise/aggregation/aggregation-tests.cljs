(ns visualise.aggregation.aggregation-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs-time.core :as t]
            [visualise.aggregation.aggregation :as a]))

(def first-day-of-year (t/date-time 2017))
(def second-day-of-year (t/date-time 2017 1 2))

(deftest aggregate-and-group
  (testing "with a single event"
    (let [event {:timestamp first-day-of-year :event-source "SOURCE"}
          {:keys [spread number-of-buckets buckets]} (a/aggregate-and-group-fn [event] 1 :event-source)]
      (is (= (:days spread) 1))
      (is (= number-of-buckets 1))
      (is (= (count buckets) 1))
      (let [{:keys [bucket-number contents]} (first buckets)
            event-with-position (first (get contents "SOURCE"))]
        (is (= bucket-number 0))
        (is (= (count contents) 1))
        (is (= (:position-in-bucket event-with-position) 0.5)))))

  (testing "with two events"
    (let [event-one {:timestamp (t/now) :event-source "SOURCE"}
          event-two {:timestamp (t/plus (t/now) (t/days 1)) :event-source "SOURCE"}
          {:keys [spread number-of-buckets buckets]} (a/aggregate-and-group-fn [event-one event-two] 1 :event-source)]
      (println buckets)
      (is (= (:days spread) 2))
      (is (= number-of-buckets 2))
      (is (= (count buckets) 2))
      (let [{:keys [bucket-number contents]} (last buckets)
            event-with-position (first (get contents "SOURCE"))]
        (is (= bucket-number 1))
        (is (= (count contents) 1))
        (is (= (:timestamp event-with-position) second-day-of-year))
        (is (= (:position-in-bucket event-with-position) 0.5)))))

  )