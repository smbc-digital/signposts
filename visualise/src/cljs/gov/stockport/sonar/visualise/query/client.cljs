(ns gov.stockport.sonar.visualise.query.client
  (:require [ajax.core :refer [GET POST]]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(defn query [query handler]
  (POST "/query"
        {:headers         {"Content-Type" "application/json"}
         :format          :json
         :response-format :json
         :keywords?       true
         :handler         (fn [response] (handler response))
         :body            (->json query)}))

(defn search [aquery handler]
  (query aquery handler))