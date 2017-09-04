(ns gov.stockport.sonar.visualise.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(def secure-cookies false)

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))
