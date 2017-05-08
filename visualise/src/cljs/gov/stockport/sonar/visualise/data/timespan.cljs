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

; doesn't work very well as it's far too drawy - need to interact with flot at a lower level
; or use the navigate plugin
(defn stop-motion-animate
  ([func] (stop-motion-animate func 5))
  ([func counter]
   (func)
   (when (> counter 0)
     (js/setTimeout (fn [] (stop-motion-animate func (dec counter))) 10))))

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

(defn reset [!timespan]
  (swap! !timespan (fn [{:keys [from-date to-date] :as timespan}]
                     (assoc timespan
                       :selected-from from-date
                       :selected-to to-date))))

(defn control-state [!timespan]
  (let [{:keys [selected-from selected-to from-date to-date]} @!timespan
        fully-out? (and (or (t/= selected-from from-date) (t/before? selected-from from-date))
                        (or (t/= selected-to to-date) (t/after? selected-to to-date)))
        fully-left? (or (t/= selected-from from-date) (t/before? selected-from from-date))
        fully-right? (or (t/= selected-to to-date) (t/after? selected-to to-date))
        fully-in? false]
    {:scroll-left  (if fully-left? :disabled :enabled)
     :scroll-right (if fully-right? :disabled :enabled)
     :reset        (if fully-out? :disabled :enabled)
     :zoom-in      (if fully-in? :disabled :enabled)
     :zoom-out     (if fully-out? :disabled :enabled)}))

