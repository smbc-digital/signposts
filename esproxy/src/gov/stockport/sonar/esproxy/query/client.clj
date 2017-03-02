(ns gov.stockport.sonar.esproxy.query.client
  (:require [clj-http.client :as http]
            [base64-clj.core :refer [encode]]))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (encode (str (:username @!creds) ":" (:password @!creds))))})

(def query-handler
  (fn [{:keys [body]}]
    (http/post "http://localhost:9200/events-*/_search"
               {:headers (authorisation-header)
                :body    (slurp body)})))
