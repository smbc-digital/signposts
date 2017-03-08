(ns gov.stockport.sonar.esproxy.app
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [gov.stockport.sonar.esproxy.routes :refer [app-handler]]
            [gov.stockport.sonar.esproxy.middleware :refer [with-middleware]])
  (:gen-class))

(defonce !server (atom nil))

(defn bounce-server []
  (if-let [server @!server] (.stop server))
  (reset! !server (run-jetty
                    (with-middleware app-handler)
                    {:port 3010 :join? false})))

(defn -main [& _]
  (bounce-server))