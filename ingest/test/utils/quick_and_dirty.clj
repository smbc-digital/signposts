(ns quick-and-dirty
  (:require [fake-data :as fd]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojure.string :as str]))


(def !conn (atom (esr/connect "http://192.168.99.100:9200")))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn write-to-index [{:keys [event-type event-source] :as event}]
  (esd/create @!conn (str "feed_" (esname event-source)) (esname event-type) event))

(defn push-some-data []
  (map write-to-index (take 1000 (fd/timelines))))

;(cheshire.core/parse-string (:body (clj-http.client/get "http://192.168.99.100:9200/_search?q=name:Barney")) true)
