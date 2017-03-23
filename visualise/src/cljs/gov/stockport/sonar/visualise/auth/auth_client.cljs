(ns gov.stockport.sonar.visualise.auth.auth-client
  (:require [ajax.core :refer [POST]]
            [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.state :refer [initialise!]]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn- perform-post [& args]
  (apply POST args))

(defn clear-search-and-navigate-to-login-page []
  (initialise!)
  (accountant/navigate! "/login"))

(defn handle-login-response [_]
  (accountant/navigate! "/"))

(defn handle-logout-response [_]
  (clear-search-and-navigate-to-login-page))

(defn attempt-login [creds]
  (perform-post
    "/login"
    {:headers         {"Content-Type" "application/json"}
     :format          :json
     :handler         handle-login-response
     :body            (->json creds)}))

(defn logout []
  (perform-post
    "/logout"
    {:headers         {"Content-Type" "application/json"}
     :format          :json
     :handler         handle-logout-response}))

(defn error-handler [response]
  (if (= (:status response) 401)
    (do
      (clear-search-and-navigate-to-login-page))
    response))