(ns gov.stockport.sonar.visualise.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults
        (-> site-defaults (assoc-in [:security :anti-forgery] false)))
      wrap-exceptions
      wrap-reload))
