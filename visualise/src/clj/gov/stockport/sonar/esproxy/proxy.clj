(ns gov.stockport.sonar.esproxy.proxy
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [ring.util.response :refer [response]]
            ;[base64-clj.core :as o :refer [encode]]
            [buddy.core.codecs.base64 :refer [encode]]))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (String. ^bytes (encode (.getBytes (str (:username @!creds) ":" (:password @!creds)))) "UTF-8"))})

(def search-url "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch")

(defn handle-query [request]
  (if-let [query (:body request)]
    (response (c/parse-string
                (:body (http/post search-url
                                  {:headers (authorisation-header)
                                   :body    (slurp query)}))))
    (response {})))

