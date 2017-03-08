(ns gov.stockport.sonar.esproxy.query.client
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [gov.stockport.sonar.esproxy.config :refer [!config]]
            [ring.util.response :refer [response]]
            [base64-clj.core :refer [encode]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (encode (str (:username @!creds) ":" (:password @!creds))))})

(def search-url (str (get-in @!config [:elastic-search :url]) "/events-*/_search"))

(defn query-handler [request]
  (if-let [query (:body request)]
    (response (c/parse-string
                (:body (http/post search-url
                                                {:headers (authorisation-header)
                                                 :body    (slurp query)}))))
    (response {})))

(defn protected-query-handler [request]
  (when (not (authenticated? request))
    (throw-unauthorized {:message "tsk tsk"}))
  (query-handler request))