(ns gov.stockport.sonar.esproxy.auth.auth-middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.core.keys :as keys]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [gov.stockport.sonar.esproxy.routes :refer [not-auth-403]]))

(def passphrase (codecs/bytes->hex (hash/sha256 "secret"))) ; passphrase for the key pair

; Generate aes128 encrypted private key
; protected with passphrase from above
; openssl genrsa -aes128 -out privkey.pem 2048

; Generate public key from previously created private key.
; openssl rsa -pubout -in privkey.pem -out pubkey.pem

(def privkey (keys/private-key "config/privkey.pem" passphrase))

(defn auth-fn [{:keys [user]}]
  (println user)
  (when (= "elastic" user) user))

(def jwe-authentication
  (backends/jwe {:secret               privkey
                 :authfn               auth-fn
                 :unauthorized-handler not-auth-403
                 :options              {:alg :rsa-oaep
                                        :enc :a128cbc-hs256}}))

(defn wrap-buddy-auth [handler]
  (-> handler
      (wrap-authorization jwe-authentication)
      (wrap-authentication jwe-authentication)))
