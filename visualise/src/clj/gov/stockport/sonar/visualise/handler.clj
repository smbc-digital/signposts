(ns gov.stockport.sonar.visualise.handler
  (:require [bidi.ring :refer [make-handler ->ResourcesMaybe ->Resources]]
            [ring.util.response :as rur :refer [response file-response redirect content-type]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup.page :refer [include-js include-css html5]]
            [gov.stockport.sonar.visualise.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [gov.stockport.sonar.esproxy.proxy :as proxy]
            [gov.stockport.sonar.auth.login-handler :as login]
            [buddy.auth :refer [authenticated?]]
            [gov.stockport.sonar.auth.auth-middleware :refer [wrap-buddy-auth]]
            [clojure.edn :as edn])
  (:import (java.util UUID)))

(defonce version (UUID/randomUUID))

(def mount-target
  [:div#app
   [:center [:i.fa.fa-spin.fa-refresh.fa-5x]]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   [:meta {:http-equiv "X-UA-Compatible"
           :content    "IE=Edge"}]
   (include-css
     "/css/bootstrap.min.css"
     "/css/font-awesome.min.css"
     (str (if (env :dev) "/css/site.css" "/css/site.min.css") "?v=" version))])

(defn html [content]
  (fn [_] (-> (response content)
              (content-type "text/html")
              (assoc-in [:cookies "csrf"] *anti-forgery-token*))))

(defn loading-page []
  (html
    (html5
      (head)
      [:body
       mount-target
       (include-js (str "/js/app.js?v=" version))])))

(defn cards-page []
  (html
    (html5
      (head)
      [:body
       mount-target
       (include-js "/js/app_devcards.js")])))

(def not-found-404
  (fn [_]
    (-> (rur/not-found "Oops! Not Found!") (content-type "text/html"))))

(def routes ["" [["/" :app]
                 ["/login" {:get  :login
                            :post :do-login}]
                 ["/logout" {:post :do-logout}]
                 ["/query" {:post :es-query}]
                 ["/keep-alive" {:post :keep-alive}]
                 ["/signposting-config" {:get :signposting-config}]
                 ["" (->ResourcesMaybe {:prefix "public/"})]
                 [true :404]]])

(defn redirect-if-not-auth [handler]
  (fn [req]
    (if (not (authenticated? req))
      (redirect "/login")
      (handler req))))

(def handlers {:login              (loading-page)
               :do-login           login/handle-login
               :do-logout          login/handle-logout
               :signposting-config (fn [_] (response (edn/read-string (slurp "signposting-config.edn"))))
               :404                not-found-404
               :app                (redirect-if-not-auth (loading-page))
               :es-query           proxy/handle-query-request
               :keep-alive         proxy/handle-keep-alive})

(def app-handler (make-handler routes (fn [handler-key-or-handler] (get handlers handler-key-or-handler handler-key-or-handler))))

(defn wrap-common-middleware [handler]
  (-> handler
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-buddy-auth)))

(def app (-> app-handler
             (wrap-common-middleware)
             (wrap-middleware)))