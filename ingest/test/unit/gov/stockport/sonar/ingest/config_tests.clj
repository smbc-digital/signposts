(ns gov.stockport.sonar.ingest.config-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.config :as config]))

(facts "about configuration"
       (fact "it should load common config"
             (keys (config/load-config)) => (contains #{:elastic-search})
             (provided
               (config/current-user-name) => "unknown-user"))
       (fact "it should override config on a per user basis"
             (:inbound-dir (config/load-config)) => "/tmp/sonar-integration-test"
             (provided
               (config/current-user-name) => "rpfilipp")))