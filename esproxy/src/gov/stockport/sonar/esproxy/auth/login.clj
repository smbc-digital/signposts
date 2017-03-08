(ns gov.stockport.sonar.esproxy.auth.login
  (:require [buddy.sign.jwt :as jwt]
            [cheshire.core :as json]
            [ring.util.response :refer [response]]
            [buddy.core.keys :as keys]
            [cheshire.core :as c]))

(def pubkey (keys/public-key "config/pubkey.pem"))

(defn find-user [u p] {:id u})

(defn login-handler
  [request]
  (if-let [creds (:body request)]
    (let [{:keys [username password]} (c/parse-string (slurp creds) true)
          user (find-user username password)
          token (jwt/encrypt {:user (:id user)} pubkey
                             {:alg :rsa-oaep :enc :a128cbc-hs256})]
      (response {:token token}))))