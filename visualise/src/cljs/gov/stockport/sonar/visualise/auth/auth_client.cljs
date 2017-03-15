(ns gov.stockport.sonar.visualise.auth.auth-client
  (:require [ajax.core :refer [POST]]
            [accountant.core :as accountant]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn- perform-login [& args]
  (apply POST args))

(defn handle-login-response [_]
  (accountant/navigate! "/"))

(defn attempt-login [creds]
  (perform-login
    "/login"
    {:headers         {"Content-Type" "application/json"}
     :format          :json
     :response-format :json
     :keywords?       true
     :handler         handle-login-response
     :body            (->json creds)}))


