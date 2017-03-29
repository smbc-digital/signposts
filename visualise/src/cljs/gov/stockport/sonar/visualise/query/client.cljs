(ns gov.stockport.sonar.visualise.query.client
  (:require [gov.stockport.sonar.visualise.util.ajax :refer [post]]))

(defn search [query query-results-handler]
  (post "/query"
        {:body            query
         :handler         query-results-handler
         :response-format :json
         :keywords?       true}))