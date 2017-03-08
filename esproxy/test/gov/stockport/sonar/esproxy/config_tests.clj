(ns gov.stockport.sonar.esproxy.config-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.config :as config]))

(facts "about configuration"
       (fact "it should load common config"
             (keys (config/load-config)) => (contains #{:elastic-search})
             (provided
               (config/current-user-name) => "unknown-user")))
