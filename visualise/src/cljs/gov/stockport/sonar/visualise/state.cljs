(ns gov.stockport.sonar.visualise.state
  (:require [reagent.core :as r]
            [ajax.core :refer [GET]]
            [hodgepodge.core :refer [local-storage clear!]]
            ))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))
(defonce !status (r/atom {}))
(defonce !search-control-state (r/atom {}))
(defonce !login-error (r/atom 0))
(defonce !signposting-config (atom {}))

(defn- load-signposting-configuration []
  (GET "/signposting-config" {:response-format :json
                              :keywords?       true
                              :handler         (fn [response] (reset! !signposting-config response))}))




(defn initialise! []
  (reset! !app {})
  (reset! !data {})
  (reset! !status {})
  (reset! !login-error 0)
  (reset! !search-control-state {})
  (load-signposting-configuration)
  )

(defn refresh-status! []
  (GET "/status" {:response-format :json
                  :keywords?       true
                  :handler         (fn [response] (reset! !status response))}))
