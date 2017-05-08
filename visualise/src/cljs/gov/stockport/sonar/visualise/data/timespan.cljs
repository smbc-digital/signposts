(ns gov.stockport.sonar.visualise.data.timespan
  (:require [cljs-time.core :as t]))

(defn from-data [{:keys [result]}]
  (let [times (map :timestamp result)]
    (if (not-empty times)
      (let [start (t/minus (apply min times) (t/months 1))
            end (t/plus (apply max times) (t/months 1))]
        {:from-date     start
         :selected-from start
         :to-date       end
         :selected-to   end}))))

(def zoom-and-scroll-period (t/months 6))

(defn zoom-in [!timespan]
  (swap! !timespan (fn [{:keys [selected-from selected-to] :as timespan}]
                     (assoc timespan
                       :selected-from (t/plus selected-from zoom-and-scroll-period)
                       :selected-to (t/minus selected-to zoom-and-scroll-period)))))

(defn zoom-out [!timespan]
  (swap! !timespan (fn [{:keys [selected-from selected-to] :as timespan}]
                     (assoc timespan
                       :selected-from (t/minus selected-from zoom-and-scroll-period)
                       :selected-to (t/plus selected-to zoom-and-scroll-period)))))

(defn scroll-left [!timespan]
  (swap! !timespan (fn [{:keys [selected-from selected-to] :as timespan}]
                     (assoc timespan
                       :selected-from (t/minus selected-from zoom-and-scroll-period)
                       :selected-to (t/minus selected-to zoom-and-scroll-period)))))

(defn scroll-right [!timespan]
  (swap! !timespan (fn [{:keys [selected-from selected-to] :as timespan}]
                     (assoc timespan
                       :selected-from (t/plus selected-from zoom-and-scroll-period)
                       :selected-to (t/plus selected-to zoom-and-scroll-period)))))

