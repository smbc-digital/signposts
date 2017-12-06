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
      (assoc! local-storage :login-message "Your session has timed out. Please login to continue."))
      )
    (do
      (if (= 401 (:login-error local-storage ))
        (assoc! local-storage :login-message "You've entered an incorrect username or password")))

    )
  (.assign js/window.location "/login")
  )



(defn navigate-to-home-page []
    (dissoc! local-storage :login-error)
    (dissoc! local-storage :login-message)
    (accountant/navigate! "/"))