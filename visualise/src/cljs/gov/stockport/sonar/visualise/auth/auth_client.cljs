(ns gov.stockport.sonar.visualise.auth.auth-client
  (:require [ajax.core :refer [POST]]
            [accountant.core :as accountant]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn- perform-post [& args]
  (apply POST args))

(defn handle-login-response [_]
  (accountant/navigate! "/"))

(defn handle-logout-response [_]
  (accountant/navigate! "/login"))

(defn attempt-login [creds]
  (perform-post
    "/login"
    {:headers         {"Content-Type" "application/json"}
     :format          :json
     :handler         handle-login-response
     :error-handler   (fn [resp] (println resp))
     :body            (->json creds)}))

(defn logout []
  (perform-post
    "/logout"
    {:headers         {"Content-Type" "application/json"}
     :format          :json
     :handler         handle-logout-response
     :error-handler   (fn [resp] (println resp))}))