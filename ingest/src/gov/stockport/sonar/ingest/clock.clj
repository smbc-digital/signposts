(ns gov.stockport.sonar.ingest.clock
  (:require [clj-time.core :as t]))

(def !now (atom nil))

(defn now []
  (or @!now (t/now)))

(defn freeze! [now]
  (reset! !now now))

(defn thaw! []
  (reset! !now nil))

(defn advance! [interval]
  (swap! !now #(t/plus % interval)))