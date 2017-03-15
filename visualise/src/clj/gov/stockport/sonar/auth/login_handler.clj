(ns gov.stockport.sonar.auth.login-handler
  (:require [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response]]
            [buddy.core.keys :as keys]
            [gov.stockport.sonar.auth.session-manager :as sm]
            [cheshire.core :refer [parse-string]]))

(def pubkey (keys/public-key "config/pubkey.pem"))

(defn handle-login [request]
  (if-let [creds (:body request)]
    (let [session (sm/create-session (parse-string (slurp creds) true))
          token (jwt/encrypt {:user session} pubkey
                             {:alg :rsa-oaep :enc :a128cbc-hs256})]
      (assoc-in (response "") [:cookies "token"] token))))
