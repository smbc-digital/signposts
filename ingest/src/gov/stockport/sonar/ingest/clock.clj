(ns gov.stockport.sonar.ingest.clock
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(def !now (atom nil))

(defn now []
  (or @!now (t/now)))

(defn now-millis []
  (c/to-long (now)))

(defn freeze! [now]
  (reset! !now now))

(defn thaw! []
  (reset! !now nil))

(defn advance! [interval]
  (swap! !now #(t/plus % interval)))