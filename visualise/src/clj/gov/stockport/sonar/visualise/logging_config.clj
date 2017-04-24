(ns gov.stockport.sonar.visualise.logging-config
  (:require [taoensso.timbre :as timbre]))

(defn configure-logging! []

  (timbre/set-level! :info)

  (timbre/merge-config! {:level :info})

  (timbre/info "using timbre for logging - see 'gov.stockport.sonar.visualise.logging-config"))
