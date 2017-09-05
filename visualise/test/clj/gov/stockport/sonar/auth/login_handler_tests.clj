(ns gov.stockport.sonar.auth.login-handler-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.auth.keys :as keys]
            [gov.stockport.sonar.auth.login-handler :as lh]
            [gov.stockport.sonar.auth.session-manager :as sm]
            [gov.stockport.sonar.esproxy.proxy :as p]
            [buddy.auth :refer [throw-unauthorized]]
            [buddy.sign.jwt :as jwt]))

(facts "about the login handler"

       (fact "it does not create a session when given invalid creds"
             (lh/handle-login {:body ..creds..}) => (throws Exception #"Unauthorized")

             (provided
               (p/is-valid-elastic-search-user? ..creds..) => false))

       (fact "it creates a session and returns a secure http only token"
             (with-redefs [gov.stockport.sonar.visualise.middleware/secure-cookies true]
               (lh/handle-login {:body ..creds..}) =>
               {:status 200 :body "" :headers {} :cookies {"token" {:value     ..token..
                                                                    :secure    true
                                                                    :http-only true}}}

               (provided
                 (p/is-valid-elastic-search-user? ..creds..) => true
                 (sm/create-session ..creds..) => ..session..
                 (jwt/encrypt {:user ..session..} @keys/pubkey
                              {:alg :rsa-oaep :enc :a128cbc-hs256}) => ..token..))))