(ns gov.stockport.sonar.esproxy.routes
  (:require [bidi.ring :refer [make-handler]]
            [ring.util.response :refer [response status not-found content-type]]
            [gov.stockport.sonar.esproxy.query.client :refer [query-handler protected-query-handler]]
            [gov.stockport.sonar.esproxy.auth.login :refer [login-handler]]))

(def not-found-404
  (fn [_]
    (not-found "Oops! Not Found!")))

(def not-auth-403
  (fn [_ _]
    (-> (response "Oops! Not Authorized!")
        (status 403)
        (content-type "text/plain"))))

(def api ["" [["/query" {:post :query}]
              ["/pquery" {:post :pquery}]
              ["/login" {:post :login}]
              [true :404]]])

(def handlers {:query  query-handler
               :pquery protected-query-handler
               :login  login-handler
               :404    not-found-404})

(def app-handler (make-handler api handlers))