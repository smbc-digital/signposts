(ns gov.stockport.sonar.esproxy.proxy-tests
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [gov.stockport.sonar.esproxy.proxy :as proxy]
            [gov.stockport.sonar.auth.session-manager :as sm]))

(fact "about sending queries to elastic search"

      (fact "should add auth credentials to the outbound request"

            (proxy/handle-query {:identity ..session..
                                 :body     {}}) => {:body {"some" "value"} :status 200 :headers {}}

            (provided
              (http/post "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch" {:headers {"Authorization" ..creds..}
                                                                                                    :body    "{}"}) => {:body "{\"some\":\"value\"}"}
              (sm/get-credentials ..session..) => ..creds..))

      (fact "should not send empty query if there is no body supplied"

            (proxy/handle-query {}) => {:body {} :status 200 :headers {}}))