(ns gov.stockport.sonar.esproxy.proxy
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [ring.util.response :refer [response]]
            [buddy.core.codecs.base64 :as b64]
            [gov.stockport.sonar.auth.session-manager :as sm]))

(def search-url "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch")

(defn auth-header [{:keys [username password]}]
  (str "Basic " (String. ^bytes (b64/encode (str username ":" password)) "UTF-8")))

(defn handle-query [{session :identity :as request}]
  (if-let [query (:body request)]
    (let [result (http/post search-url
                            {:headers {"Authorization" (auth-header (sm/get-credentials session))}
                             :body    (c/generate-string query)})]

      (response (c/parse-string (:body result))))
    (response {})))

