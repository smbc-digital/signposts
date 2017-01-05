(ns ingest.config-test
  (:require [midje.sweet :refer :all]
            [ingest.config :as config]))

(facts "about configuration"
       (fact "it should load common config"
             (keys (config/load-config)) => (contains #{:elastic-search})
             (provided
               (config/current-user-name) => "unknown-user"))
       (fact "it should override config on a per user basis"
             (get-in (config/load-config) [:elastic-search :url]) => "http://192.168.99.100:9200"
             (provided
               (config/current-user-name) => "rpfilipp")))