(ns gov.stockport.sonar.visualise.state
  (:require [reagent.core :as r]
            [ajax.core :refer [GET]]
            [hodgepodge.core :refer [local-storage clear!]]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))
(defonce !status (r/atom {}))
(defonce !search-control-state (r/atom {}))
(defonce !login-error (r/atom 0))
(defonce !signposting-config (atom {}))
(defonce !search-history (r/atom ()))
(defonce !search-options (r/atom []))
(defonce !show-search-control (r/atom 0))
(defn- load-signposting-configuration []
  (GET "/signposting-config" {:response-format :json
                              :keywords?       true
                              :handler         (fn [response] (reset! !signposting-config response))}))


(defn navigate-to-login-page []
   (if (not= js/window.location.pathname "/login")
      (assoc! local-storage :login-message "Your session has timed out. Please login to continue."))
  (.assign js/window.location "/login"))

(defn error-handler [{:keys [status status-text]}]
  (assoc! local-storage :login-error status)
  (if (= status 401)
    (navigate-to-login-page)
    (println (str "something bad happened: " status " " status-text)))
    )



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
                  :handler        (fn [response](reset! !status response))
                  :error-handler error-handler }))