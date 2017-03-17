(ns gov.stockport.sonar.esproxy.proxy
  (:require [clj-http.client :as http]
            [cheshire.core :as c]
            [ring.util.response :refer [response]]
            [slingshot.slingshot :refer [try+]]
            [buddy.auth :refer [throw-unauthorized]]
            [buddy.core.codecs.base64 :as b64]
            [gov.stockport.sonar.auth.session-manager :as sm]))

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
  (if-let [query (:body request)]
    (response (perform-query (sm/get-credentials session) query))
    (response {})))