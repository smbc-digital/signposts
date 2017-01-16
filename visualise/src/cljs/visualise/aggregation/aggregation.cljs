(ns visualise.aggregation.aggregation
  (:require [cljs-time.core :as t]
            [cljs-time.format :as f]
            [cljs-time.periodic :as p]
            [visualise.aggregation.date-spread :refer [date-spread quarter h1?]]))

(defn bucket-content [from to events]
  (let [days (t/in-days (t/interval from to))
        content (filter #(t/within? from to (:timestamp %)) events)]
    (map
      (fn [{:keys [timestamp] :as event}]
        (assoc event :position-in-bucket (/ (t/in-days (t/interval from timestamp)) days)))
      content)))

(defn aggregate-into-periods [seq qty name-fn group-fn ascending-events]
  (let [start (take qty seq)
        end (map #(t/minus % (t/seconds 1)) (rest (take qty seq)))
        earlier (fn [[a _] [b _]] (t/before? a b))]
    (map
      (fn [[idx [from to]]]
        {:bucket-number idx
         :bucket-name   (name-fn from to)
         :lower         from
         :upper         to
         :contents      (group-by group-fn (bucket-content from to ascending-events))})
      (zipmap (range) (sort earlier (zipmap start end))))))

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

(defn aggregate-and-group-fn [event-series zoom event-grouping-function]
  (let [ascending-events (sort-by :timestamp t/before? event-series)
        spread (date-spread (:timestamp (first ascending-events)) (:timestamp (last ascending-events)))
        aggregated-events ((zoom-based-resolution zoom spread) spread event-grouping-function ascending-events)]
    {:spread            spread
     :number-of-buckets (count aggregated-events)
     :buckets           aggregated-events}))

(def aggregate-and-group (memoize aggregate-and-group-fn))