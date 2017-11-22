(ns gov.stockport.sonar.visualise.util.navigation
  (:require [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.state :refer [initialise!]]
            [hodgepodge.core :refer [local-storage clear!]]))

(defn navigate-to-login-page []
  ;(initialise!)
  ; use to window.location.assign() to force a reload
  ; of the login page which in turn cleans any
  ; anti-forgery and invalid session tokens
  ; say if the server is re-started
  (if (not= js/window.location.pathname "/login")
    (do
      (if (= 401 (:login-error local-storage ))
      (assoc! local-storage :login-message "You have been timed out")))
      (.assign js/window.location "/login"))
    (do
      (if (= 401 (:login-error local-storage ))
        (assoc! local-storage :login-message "You've entered an incorrect username or password")))
      )



(defn navigate-to-home-page []
    (dissoc! local-storage :login-error)
    (accountant/navigate! "/"))