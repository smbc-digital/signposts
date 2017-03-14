(ns gov.stockport.sonar.auth.login
  (:require [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response]]
            [buddy.core.keys :as keys]
            [cheshire.core :refer [parse-string]]))

(def pubkey (keys/public-key "config/pubkey.pem"))

(defn find-user [u p] {:id u})

(defn do-login
  [request]
  (if-let [creds (:body request)]
    (let [{:keys [username password]} (parse-string (slurp creds) true)
          user (find-user username password)
          token (jwt/encrypt {:user (:id user)} pubkey
                             {:alg :rsa-oaep :enc :a128cbc-hs256})]
      (assoc-in (response "") [:cookies "token"] token))))
