(ns gov.stockport.sonar.esproxy.proxy
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [ring.util.response :refer [response]]
            [gov.stockport.sonar.auth.session-manager :as sm]))

(def search-url "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch")

(defn handle-query [{session :identity :as request}]
  (let [query (:body request)
        result (http/post search-url
                          {:headers {"Authorization" (sm/get-credentials session)}
                           :body    (slurp query)})]
    (response (c/parse-string (:body result)))))

