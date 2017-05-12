(ns gov.stockport.sonar.visualise.server
  (:require [gov.stockport.sonar.visualise.handler :refer [app]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [gov.stockport.sonar.visualise.logging-config :refer [configure-logging!]])
  (:gen-class))

(configure-logging!)

(def !server (atom nil))

(defn bounce-server []
  (if-let [server @!server] (.stop server))
  (reset!
    !server
    (let [port (Integer/parseInt (or (env :port) "3000"))]
      (run-jetty app {:host "127.0.0.1" :port port :join? false}))))

(defn -main [& args]
  (bounce-server))
