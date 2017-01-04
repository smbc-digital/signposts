(ns bootstrap-demo
  (:require [ingest.config :refer [!config]]
            [quick-and-dirty :as qad]
            [users-and-groups :as uag])
  (:gen-class))

(defn bootstrap []
  (println "Bootstrapping...")
  (println "pushing some fake data")
  (qad/push-some-fake-data 50)
  (println "creating users and roles")
  (uag/create-ro-roles)
  (uag/create-ro-users)
  (uag/create-full-role)
  (uag/create-full-user))

(defn -main [& args]
  (if (:use-fake-data @!config)
    (bootstrap)
    (println "system not in fake data mode...")))

