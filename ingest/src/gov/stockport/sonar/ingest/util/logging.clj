(ns gov.stockport.sonar.ingest.util.logging
  (:require [clojure.pprint :as pp]))

(defn log [& args]
  (println args))

(defn plog [& args]
  (pp/pprint args))

(defmacro log-time
  "Evaluates expr and prints the time it took.  Returns the value of
 expr."
  {:added "1.0"}
  [msg expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (log (str ~msg " took: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))


