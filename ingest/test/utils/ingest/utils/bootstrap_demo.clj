(ns ingest.utils.bootstrap-demo
  (:require [ingest.config :refer [!config]]
            [ingest.client.elastic-search-client :as esc]
            [ingest.utils.quick-and-dirty :as qad]
            [ingest.utils.users-and-groups :as uag]
            [ingest.utils.wait-for :refer [wait-for]])
  (:gen-class))

(defn bootstrap []
  (println "Bootstrapping...")
  (let [es-avail (wait-for #(= 200 (esc/ping)) :interval 2 :timeout 30)]
    (if es-avail
      (do
        (Thread/sleep 2000)
        (println "pushing some fake data")
        (qad/push-some-fake-data 50000)
        (uag/create-demo-users-and-groups)
        (qad/create-kibana-index)
        )
      (println "Elastic Search did not become available in time, sorry."))))

(defn -main [& args]
  (if (:use-fake-data @!config)
    (bootstrap)
    (println "system not in fake data mode...")))

