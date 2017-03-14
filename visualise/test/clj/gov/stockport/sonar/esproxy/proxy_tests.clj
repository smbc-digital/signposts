(ns gov.stockport.sonar.esproxy.proxy-tests
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [gov.stockport.sonar.esproxy.proxy :as proxy]))

(fact "about sending queries to elastic search"

      (fact "should add auth credentials to the outbound request"

            (proxy/query-handler {:body (.getBytes "{}")}) => {:body {"some" "value"} :status 200 :headers {}}

            (provided
              (http/post "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch" {:headers ..auth-header..
                                                                                                    :body    "{}"}) => {:body "{\"some\":\"value\"}"}
              (proxy/authorisation-header) => ..auth-header..))

      (fact "should not send empty query if there is no body supplied"

            (proxy/query-handler {}) => {:body {} :status 200 :headers {}}))