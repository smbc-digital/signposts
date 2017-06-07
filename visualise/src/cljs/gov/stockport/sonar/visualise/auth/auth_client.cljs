(ns gov.stockport.sonar.visualise.auth.auth-client
  (:require [gov.stockport.sonar.visualise.util.ajax :as ajax]
            [gov.stockport.sonar.visualise.util.navigation :refer [navigate-to-home-page navigate-to-login-page]]))

(defn handle-successful-login [_]
  (navigate-to-home-page))

(defn handle-successful-logout [_]
  (navigate-to-login-page))

(defn login [creds]
  (ajax/ajax-post "/login" {:body creds
                       :handler   handle-successful-login}))

(defn logout []
  (ajax/ajax-post "/logout" {:handler handle-successful-logout}))