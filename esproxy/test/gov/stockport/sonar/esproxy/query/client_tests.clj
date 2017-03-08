(ns gov.stockport.sonar.esproxy.query.client-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.esproxy.query.client :as client]
            [clj-http.client :as http]))

(fact "about sending queries to elastic search"

      (fact "should add auth credentials to the outbound request"

            (client/query-handler {:body (.getBytes "{}")}) => {:body {"some" "value"} :status 200 :headers {}}

            (provided
              (http/post "http://localhost:9200/events-*/_search" {:headers ..auth-header..
                                                                   :body    "{}"}) => {:body "{\"some\":\"value\"}"}
              (client/authorisation-header) => ..auth-header..))

      (fact "should not send empty query if there is no body supplied"

            (client/query-handler {}) => {:body {} :status 200 :headers {}}))