(ns gov.stockport.sonar.visualise.util.navigation
  (:require [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.state :refer [initialise!]]))

(defn navigate-to-login-page []
  (initialise!)
  ; use to window.location.assign() to force a reload
  ; of the login page which in turn cleans any
  ; anti-forgery and invalid session tokens
  ; say if the server is re-started
  (.assign js/window.location "/login"))

(defn navigate-to-home-page []
  (accountant/navigate! "/"))