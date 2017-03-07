(ns gov.stockport.sonar.esproxy.app
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [gov.stockport.sonar.esproxy.handler :refer [handler]]
            [gov.stockport.sonar.esproxy.middleware :refer [with-middleware]]))

(defonce !server (atom nil))

(defn bounce-server []
  (if-let [server @!server] (.stop server))
  (reset! !server (run-jetty
                    (with-middleware handler)
                    {:port 3010 :join? false})))

(defn -main [& _]
  (bounce-server))