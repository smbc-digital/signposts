(ns ingest.client.elastic-search-client
  (:require [clj-http.client :as http]
            [ingest.config :refer [!config]]
            [base64-clj.core :as b64]
            [cheshire.core :refer [generate-string]]))

(defn auth-header []
  (let [{:keys [username password]} (:elastic-search @!config)]
    {:Authorization (str "Basic " (b64/encode (str username ":" password)))}))

(defn es-url-for [path]
  (let [{url :url} (:elastic-search @!config)]
    (str url path)))

(defn post-json-to-es [{:keys [path payload]}]
  (http/post (es-url-for path)
             {:content-type :json
              :headers      (auth-header)
              :body         (generate-string payload)}))

(defn delete-to-es [{:keys [path]}]
  (http/delete (es-url-for path)
               {:headers (auth-header)}))

(defn ping []
  (try
    (println "ping es to see if it is awake yet...")
    (:status (http/get (es-url-for "/")
                       {:headers (auth-header)}))
    (catch Exception _)))

