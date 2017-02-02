(ns gov.stockport.sonar.ingest.elastic.client
  (:require [clj-http.client :as http]
            [base64-clj.core :as b64]
            [cheshire.core :as c]
            [gov.stockport.sonar.ingest.config :refer [!config]]))

(defn json->map-with-keywords [json]
  (c/parse-string json true))

(defn auth-header []
  (let [{:keys [username password]} (:elastic-search @!config)]
    {:Authorization (str "Basic " (b64/encode (str username ":" password)))}))

(defn es-url-for [path]
  (let [{url :url} (:elastic-search @!config)]
    (str url path)))

(defn report [{:keys [status body]}]
  (let [es-result (json->map-with-keywords body)
        total (count (:items es-result))
        errors (count (filter :error (:items es-result)))]
    {:status           status
     :took-ms          (:took es-result)
     :records-indexed  (- total errors)
     :records-rejected errors}))

(defn post-bulk-data [bulk-data]
  (report
    (http/post (es-url-for "/_bulk")
               {:headers (auth-header)
                :body    bulk-data})))

