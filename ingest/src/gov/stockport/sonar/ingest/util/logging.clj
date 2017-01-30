(ns gov.stockport.sonar.ingest.util.logging)

(defn log [& args]
  (println args))

(defn plog [& args]
  (clojure.pprint/pprint args))

