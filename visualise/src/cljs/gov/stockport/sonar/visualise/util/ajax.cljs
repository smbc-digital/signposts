(ns gov.stockport.sonar.visualise.util.ajax
  (:require [ajax.core :refer [POST]]
            [gov.stockport.sonar.visualise.util.navigation :refer [navigate-to-login-page]]
            [reagent.cookies :refer [get-raw]]
            [cemerick.url :refer [url-decode]]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn perform-post [& args]
  (apply POST args))

(defn with-common-options [request]
  (assoc-in
    (merge request {:format :json})
    [:headers "Content-Type"] "application/json"))

(defn with-csrf-header [request]
  (if-let [csrf-token (url-decode (get-raw :csrf))]
    (assoc-in request [:headers "X-CSRF-Token"] csrf-token)
    request))

(defn with-json [{:keys [body] :as request}]
  (assoc request :body (->json body)))

(defn default-error-handler [{:keys [status] :as response}]
  (if (= status 401)
    (navigate-to-login-page)
    response))

(defn with-error-handling [request]
  (assoc request :error-handler default-error-handler))

(defn post [url request]
  (perform-post
    url
    (-> request
        (with-common-options)
        (with-csrf-header)
        (with-json)
        (with-error-handling))))
