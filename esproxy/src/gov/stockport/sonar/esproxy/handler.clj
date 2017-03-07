(ns gov.stockport.sonar.esproxy.handler
  (:require [bidi.ring :refer [make-handler]]
            [ring.util.response :refer [response not-found]]
            [gov.stockport.sonar.esproxy.query.client :refer [query-handler]]))

(def not-found-404
  (fn [_]
    (not-found "oops!")))

(def api ["" [["/query" {:post    :query
                         :get     :query
                         :options :query}]
              [true :404]]])

(def handlers {:query query-handler
               :404   not-found-404})

(def handler (make-handler api handlers))