(ns quick-and-dirty
  (:require [fake-data :as fd]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]))

(defn push-some-data []
  (let [conn (esr/connect "http://192.168.99.100:9200")
        tls (take 50 (fd/timelines))]
    (map (fn [tl]
           (map #(esd/create conn "people" "event" %) tl)) tls)))
