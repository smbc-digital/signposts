(ns gov.stockport.sonar.visualise.util.keep-alive
  (:require [gov.stockport.sonar.visualise.query.client :refer [keep-alive]]))

(defn with-keep-alive [delegate]
  (fn [& args]
    (keep-alive)
    (apply delegate args)))