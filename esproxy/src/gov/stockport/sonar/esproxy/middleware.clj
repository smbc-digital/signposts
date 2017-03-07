(ns gov.stockport.sonar.esproxy.middleware
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.cors :refer [wrap-cors]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn with-middleware [handler]
  (-> handler
      (wrap-json-response)
      (wrap-defaults api-defaults)
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods #{:get :post})
      (wrap-exceptions)
      (wrap-reload)))
