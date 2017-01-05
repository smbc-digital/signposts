(ns ingest.client.kibana-client
  (:require [clj-http.client :as http]
            [ingest.config :refer [!config]]
            [ingest.client.elastic-search-client :refer [auth-header]]
            [cheshire.core :refer [generate-string]]))

(defn create-feed-index []
  (let [index-name "feed_*"])
  (http/post (str (:kibana))
             {:content-type :json
              :headers      (auth-header)
              :body         (generate-string payload)}))



