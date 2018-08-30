(ns gov.stockport.sonar.esproxy.proxy
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [ring.util.response :refer [response]]
            [slingshot.slingshot :refer [try+]]
            [buddy.auth :refer [throw-unauthorized]]
            [buddy.core.codecs.base64 :as b64]
            [gov.stockport.sonar.auth.session-manager :as sm]
            [gov.stockport.sonar.esproxy.es-query-builder :as qb]
            [gov.stockport.sonar.esproxy.es-available-data :as ad]
            [gov.stockport.sonar.visualise.gdprlog :as g]
            [taoensso.timbre :refer [info]]))

(def search-url "http://localhost:9200/events-*/_search?search_type=dfs_query_then_fetch")

(defn auth-header [{:keys [username password]}]
  (str "Basic " (String. ^bytes (b64/encode (str username ":" password)) "UTF-8")))

(defn perform-query [credentials query]
  (try+
    (let [result (http/post search-url
                            {:headers {"Authorization" (auth-header credentials)}
                             :body    (c/generate-string query)})]
      (c/parse-string (:body result)))
    (catch Object _ (throw-unauthorized))))

(defn is-valid-elastic-search-user? [creds]
  (try
    (perform-query creds {})
    true
    (catch Exception _ false)))

(defn handle-query-request [{session :identity :as request}]
  (if-let [query-defs (:body request)]
    (let [{:keys [username] :as credentials} (sm/get-credentials session)
          query (qb/build-es-query query-defs)]
      (when username
        (g/log-query username query)
        (info (str "User [" username "] performed query: " query)))
      (response (perform-query credentials query)))
    (response {})))

(defn handle-status-request [{session :identity}]
  (response
    (try+
      (let [result (http/get search-url
                             {:headers {"Authorization" (auth-header (sm/get-credentials session))}
                              :body    (c/generate-string ad/data-freshness-aggregation-query)})]
        (ad/summarise-available-data (c/parse-string (:body result) true)))
      (catch Object _ (throw-unauthorized)))))

(defn handle-keep-alive [{session :identity}]
  (sm/ping! session)
  (response {}))