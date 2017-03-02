(ns gov.stockport.sonar.esproxy.app
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [gov.stockport.sonar.esproxy.handler :refer [handler]]))

(defonce !server (atom nil))

(defn bounce-server []
  (if-let [server @!server] (.stop server))
  (reset! !server (run-jetty
                    (wrap-defaults handler api-defaults)
                    {:port 3010 :join? false})))

(defn -main [& _]
  (bounce-server))