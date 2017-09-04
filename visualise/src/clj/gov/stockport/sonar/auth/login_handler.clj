(ns gov.stockport.sonar.auth.login-handler
  (:require [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response]]
            [gov.stockport.sonar.auth.keys :refer [pubkey]]
            [gov.stockport.sonar.esproxy.proxy :refer [is-valid-elastic-search-user?]]
            [buddy.auth :refer [throw-unauthorized]]
            [gov.stockport.sonar.auth.cookies :as c]
            [gov.stockport.sonar.auth.session-manager :as sm]))

(defn handle-login [{creds :body :as request}]
  (when (not (is-valid-elastic-search-user? creds)) (throw-unauthorized))
  (let [session (sm/create-session creds)
        token (jwt/encrypt {:user session} @pubkey
                           {:alg :rsa-oaep :enc :a128cbc-hs256})]
    (c/add-cookie (response "") "token" token)))

(defn handle-logout [{session :identity}]
  (sm/logout session)
  (response ""))