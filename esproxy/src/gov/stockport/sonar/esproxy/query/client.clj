(ns gov.stockport.sonar.esproxy.query.client
  (:require [clj-http.client :as http]
            [gov.stockport.sonar.esproxy.config :refer [!config]]
            [ring.util.response :refer [response]]
            [base64-clj.core :refer [encode]]))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (encode (str (:username @!creds) ":" (:password @!creds))))})

(def search-url (str (get-in @!config [:elastic-search :url]) "/events-*/_search"))

(def query-handler
  (fn [request]
    (if-let [query (:body request)]
      (response (:body (http/post search-url
                                  {:headers (authorisation-header)
                                   :body    (slurp query)})))
      (response {}))))