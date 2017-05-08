(ns gov.stockport.sonar.visualise.util.keep-alive
  (:require [gov.stockport.sonar.visualise.query.client :refer [keep-alive]])
  (:import [goog.async Debouncer]))

(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    ;; We use apply here to support functions of various arities
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))

(def keep-alive-debounced!
  (debounce keep-alive 1000))

(defn with-keep-alive [delegate]
  (fn [& args]
    (keep-alive-debounced!)
    (apply delegate args)))