(ns gov.stockport.sonar.visualise.util.navigation
  (:require [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.state :refer [initialise!]]))

(defn navigate-to-login-page []
  (initialise!)
  (accountant/navigate! "/login"))

(defn navigate-to-home-page []
  (accountant/navigate! "/"))