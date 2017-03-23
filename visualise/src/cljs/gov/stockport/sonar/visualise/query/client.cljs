(ns gov.stockport.sonar.visualise.query.client
  (:require [ajax.core :refer [GET POST]]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn query [query handler]
  (POST "/query"
        {:headers         {"Content-Type" "application/json"}
         :format          :json
         :response-format :json
         :keywords?       true
         :handler         (fn [response] (handler response))
         :error-handler   ac/error-handler
         :body            (->json query)}))

(defn search [aquery handler]
  (query aquery handler))