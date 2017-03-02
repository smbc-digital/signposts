(ns gov.stockport.sonar.esproxy.query.client-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.query.client :as client]
            [clj-http.client :as http]))

(fact "about sending queries to elastic search"

      (fact "should add auth credentials to the outbound request"
            (client/query-handler {:body (.getBytes "{}")}) => ..response..
            (provided
              (http/post "http://localhost:9200/events-*/_search" {:headers ..auth-header..
                                                                   :body    "{}"}) => ..response..
              (client/authorisation-header) => ..auth-header..)))
