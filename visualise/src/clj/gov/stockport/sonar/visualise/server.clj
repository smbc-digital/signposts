(ns gov.stockport.sonar.visualise.server
  (:require [gov.stockport.sonar.visualise.handler :refer [app]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(def !server (atom nil))

(defn bounce-server []
  (if-let [server @!server] (.stop server))
  (reset!
    !server
    (let [port (Integer/parseInt (or (env :port) "3000"))]
      (run-jetty app {:port port :join? false}))))

(defn -main [& args]
  (bounce-server))
