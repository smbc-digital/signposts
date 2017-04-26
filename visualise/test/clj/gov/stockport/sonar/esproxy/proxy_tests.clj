(ns gov.stockport.sonar.esproxy.proxy-tests
  (:require [midje.sweet :refer :all]
            [clj-http.client :as http]
            [gov.stockport.sonar.esproxy.proxy :as proxy]
            [gov.stockport.sonar.esproxy.es-query-builder :as qb]
            [gov.stockport.sonar.auth.session-manager :as sm]
            [buddy.core.codecs.base64 :as b64]))

(fact "about sending queries to elastic search"

      (fact "adds auth credentials to the outbound request when performing query"
            (proxy/perform-query ..creds.. ..query..) => {"some" "value"}
            (provided
              (proxy/auth-header ..creds..) => ..auth-header..
              (http/post "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch"
                         {:headers {"Authorization" ..auth-header..}
                          :body    "{}"}) => {:body "{\"some\":\"value\"}"}))

      (fact "throws buddy unathorized exception when there is a problem with the query"
            (proxy/perform-query ..creds.. ..query..) => (throws Exception #"Unauthorized")
            (provided
              (proxy/auth-header ..creds..) => ..auth-header..
              (http/post "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch"
                         {:headers {"Authorization" ..auth-header..}
                          :body    "{}"}) =throws=> (Exception. "BARF CHUNKS")))

      (fact "should add auth credentials to the outbound request"

            (proxy/handle-query-request {:identity ..session..
                                         :body     ..query-defs..}) => {:body ..result.. :status 200 :headers {}}
            (provided
              (sm/get-credentials ..session..) => ..creds..
              (qb/build-es-query ..query-defs..) => ..query..
              (proxy/perform-query ..creds.. ..query..) => ..result..))

      (fact "should not send empty query if there is no body supplied"
            (proxy/handle-query-request {}) => {:body {} :status 200 :headers {}})


      (fact "builds suitable auth header for elastic search from credentials"
            (proxy/auth-header {:username "the-username" :password "the-password"}) =>
            (str "Basic " (String. ^bytes (b64/encode "the-username:the-password") "UTF-8")))

      (fact "uses simple query to determine when user is valid"
            (proxy/is-valid-elastic-search-user? ..creds..) => true
            (provided
              (proxy/perform-query ..creds.. {}) => {}))

      (fact "uses simple query to determine when user is not valid"
            (proxy/is-valid-elastic-search-user? ..creds..) => false
            (provided
              (proxy/perform-query ..creds.. {}) =throws=> (ex-info "Unauthorized" {}))))