(ns gov.stockport.sonar.visualise.middleware
  (:require [ring.middleware.defaults :refer [secure-site-defaults wrap-defaults]]))

(def secure-cookies true)

(defn wrap-middleware [handler]
  (wrap-defaults handler
                 (-> secure-site-defaults
                     (assoc-in [:security :ssl-redirect] false))))
