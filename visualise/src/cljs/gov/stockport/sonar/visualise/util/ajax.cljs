(ns gov.stockport.sonar.visualise.util.ajax
  (:require [ajax.core :refer [POST GET]]
            [gov.stockport.sonar.visualise.state :refer [!app !login-error]]
            [gov.stockport.sonar.visualise.util.navigation :refer [navigate-to-login-page]]
            [reagent.cookies :refer [get-raw]]
            [hodgepodge.core :refer [local-storage clear!]]
            [cemerick.url :refer [url-decode]]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn perform-post [& args]
  (apply POST args))

(defn perform-get [& args]
  (apply GET args))

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
  (assoc! local-storage :login-error status)
  (if (>= status 400)
    (navigate-to-login-page))
    response)

(defn with-error-handling [request]
  (assoc request :error-handler default-error-handler))

(defn wrapped-handler [handler]
  (fn [& args]
    (try
      (apply handler args)
      (finally
        (swap! !app assoc :ajax-in-progress false)))))

(defn with-in-progress [{:keys [handler error-handler] :as request}]
  (swap! !app assoc :ajax-in-progress true)
  (-> request
      (assoc :handler (wrapped-handler handler)
             :error-handler (wrapped-handler error-handler))))

(defn- with-standard-wrappers [request]
  (-> request
      (with-common-options)
      (with-csrf-header)
      (with-json)
      (with-error-handling)))

(defn ajax-post [url request]
  (perform-post
    url
    (-> request
        (with-standard-wrappers)
        (with-in-progress))))

(defn post-and-forget [url request]
  (perform-post
    url
    (-> request
        (with-standard-wrappers))))

(defn ajax-get [url request]
  (perform-get url request))