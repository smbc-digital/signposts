(ns gov.stockport.sonar.visualise.state
  (:require [reagent.core :as r]
            [ajax.core :refer [GET]]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))
(defonce !status (r/atom {}))

(defonce !signposting-config (atom {}))

(defn- load-signposting-configuration []
  (GET "/signposting-config" {:response-format :json
                              :keywords?       true
                              :handler         (fn [response] (reset! !signposting-config response))}))

(defn initialise! []
  (reset! !app {})
  (reset! !data {})
  (reset! !status {})
  (load-signposting-configuration))

(defn refresh-status! []
  (GET "/status" {:response-format :json
                  :keywords?       true
                  :handler         (fn [response] (reset! !status response))}))