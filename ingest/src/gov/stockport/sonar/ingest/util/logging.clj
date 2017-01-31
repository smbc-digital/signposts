(ns gov.stockport.sonar.ingest.util.logging
  (:require [clojure.pprint :as pp]))

(defn log [& args]
  (println args))

(defn plog [& args]
  (pp/pprint args))

