(ns gov.stockport.sonar.esproxy.proxy-tests
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [gov.stockport.sonar.esproxy.proxy :as proxy]
            [gov.stockport.sonar.auth.session-manager :as sm]
            [buddy.core.codecs.base64 :as b64]))

(fact "about sending queries to elastic search"

      (fact "should add auth credentials to the outbound request"

            (proxy/handle-query {:identity ..session..
                                 :body     {}}) => {:body {"some" "value"} :status 200 :headers {}}

            (provided
              (sm/get-credentials ..session..) => ..creds..
              (proxy/auth-header ..creds..) => ..auth-header..
              (http/post "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch"
                         {:headers {"Authorization" ..auth-header..}
                          :body    "{}"}) => {:body "{\"some\":\"value\"}"}))

      (fact "should not send empty query if there is no body supplied"
            (proxy/handle-query {}) => {:body {} :status 200 :headers {}})


      (fact "builds suitable auth header for elastic search from credentials"
            (proxy/auth-header {:username "the-username" :password "the-password"}) =>
            (str "Basic " (String. ^bytes (b64/encode "the-username:the-password") "UTF-8"))))