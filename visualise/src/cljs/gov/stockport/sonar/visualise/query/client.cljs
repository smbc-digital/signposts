(ns gov.stockport.sonar.visualise.query.client
  (:require
    [gov.stockport.sonar.visualise.util.ajax :refer [ajax-get ajax-post post-and-forget]]))

(def null-handler (fn [& _]))

(defn status [handler]
  (ajax-get "/status"
            {:handler         handler
             :response-format :json
             :keywords?       true}))

(defn search [query query-results-handler]
  (ajax-post "/query"
             {:body            query
              :handler         query-results-handler
              :response-format :json
              :keywords?       true}))

(defn keep-alive []
  (post-and-forget "/keep-alive" {:handler null-handler}))