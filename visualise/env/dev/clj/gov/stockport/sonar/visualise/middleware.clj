(ns gov.stockport.sonar.visualise.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-json-response)
      (wrap-defaults
        (-> site-defaults (assoc-in [:security :anti-forgery] false)))
      wrap-exceptions
      wrap-reload))
