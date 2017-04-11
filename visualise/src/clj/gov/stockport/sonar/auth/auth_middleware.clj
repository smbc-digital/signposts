(ns gov.stockport.sonar.auth.auth-middleware
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.util.response :as r]
            [gov.stockport.sonar.auth.keys :refer [privkey]]
            [gov.stockport.sonar.auth.session-manager :as sm])
  (:import (org.bouncycastle.jcajce.provider.util BadBlockException)))

(defn auth-fn [{session :user}]
  (when (sm/valid? session) session))

(def jwe-authentication
  (backends/jwe {:secret               @privkey
                 :authfn               auth-fn
                 :unauthorized-handler (fn [_ _] (-> (r/response "{}") (r/status 401)))
                 :options              {:alg :rsa-oaep
                                        :enc :a128cbc-hs256}}))

(defn wrap-raise-auth-token-from-cookies-to-header [handler]
  (fn [request]
    (let [request (if-let [token (get-in request [:cookies "token" :value])]
                    (assoc-in request [:headers "Authorization"] (str "Token " token))
                    request)]
      (handler request))))

(defn wrap-with-try-auth [handler]
  (let [with-auth (wrap-authentication handler jwe-authentication)]
    (fn [request]
      (try
        (with-auth request)
        (catch BadBlockException _
          (handler request))))))

(defn wrap-buddy-auth [handler]
  (-> handler
      (wrap-authorization jwe-authentication)
      (wrap-with-try-auth)
      (wrap-raise-auth-token-from-cookies-to-header)))