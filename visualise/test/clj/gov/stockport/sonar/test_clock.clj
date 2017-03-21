(ns gov.stockport.sonar.test-clock
  (:require [clj-time.core :as t]))

(def frozen-date-time (atom nil))

(defn freeze!
  ([] (freeze! (t/now)))
  ([date-time]
   (reset! frozen-date-time date-time)))

(defn thaw! []
  (reset! frozen-date-time nil))

(defn now []
  (or @frozen-date-time (t/now)))