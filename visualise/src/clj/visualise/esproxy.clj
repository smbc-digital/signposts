(ns visualise.esproxy
  (:require [ring.util.response :refer [response content-type header]]
            [clj-http.client :as http]
            [base64-clj.core :as base64]))

(def elasticsearch-proxy-path "/elasticsearch")

(def path-pattern (re-pattern (str "^" elasticsearch-proxy-path "/(.*)")))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (base64/encode (str (:username @!creds) ":" (:password @!creds))))})

(defn proxied-request [{:keys [uri body] :as req}]
  (let [request-path (last (re-matches path-pattern uri))]
    (-> (select-keys req [:request-method :content-type])
        (assoc :body (slurp body))
        (assoc :headers (authorisation-header))
        (assoc :url (str "http://localhost:9200/" request-path)))))

(def esproxy (fn [req] (http/request (proxied-request req))))

