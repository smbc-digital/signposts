(ns gov.stockport.sonar.visualise.query.client
  (:require [gov.stockport.sonar.visualise.util.ajax :refer [post]]))

(def null-handler (fn [& _]))

(defn search [query query-results-handler]
  (post "/query"
        {:body            query
         :handler         query-results-handler
         :response-format :json
         :keywords?       true}))

(defn keep-alive []
  (post "/keep-alive" {:handler null-handler}))