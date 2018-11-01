(ns gov.stockport.sonar.visualise.state
  (:require [reagent.core :as r]
            [ajax.core :refer [GET]]
            [hodgepodge.core :refer [local-storage clear!]]))
"Sets up Reagant atoms
"
(defonce !app (r/atom {}))
(defonce !data (r/atom {}))
(defonce !status (r/atom {}))
(defonce !search-control-state (r/atom {}))
(defonce !login-error (r/atom 0))
(defonce !signposting-config (atom {}))
(defonce !search-history (r/atom ()))
(defonce !search-options (r/atom []))
(defonce !show-search-control (r/atom 0))
(defonce !show-select  (r/atom 1))
(defonce !show-input  (r/atom 0))
(defonce !active-plus (r/atom 0))
(defonce !search-type  (r/atom ""))
(defonce !selected-options  (r/atom (set [])))

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
    (println (str "something bad happened: " status " " status-text))))

(defn initialise! []
  (reset! !app {})
  (reset! !data {})
  (reset! !status {})
  (reset! !login-error 0)
  (reset! !search-control-state {})
  (load-signposting-configuration))

;;
;;The Following swaps {:query-type "Name" search-term "foo" } to {:query-type :name :search-term  "foo"}
(defn name-to-keyword[search-term]
  {:query-type (keyword (:query-type search-term)) :search-term (:search-term search-term)})

(defn loop-search[search]
  (map name-to-keyword search))

(defn search-history-response[response]
  (map loop-search response))
;;
(defn refresh-status! []
  (GET "/status" {:response-format :json
                  :keywords?       true
                  :handler        (fn [response](reset! !status response))
                  :error-handler error-handler }))

(defn search-history! []
  (GET "/search-history" {:response-format :json
                  :keywords?       true
                  :handler        (fn [response](reset! !search-history (search-history-response(:search-history response))))
                  :error-handler error-handler }))