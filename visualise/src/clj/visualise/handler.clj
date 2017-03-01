(ns visualise.handler
  (:require [compojure.core :refer [GET ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [visualise.middleware :refer [wrap-middleware]]
            [visualise.esproxy :refer [esproxy]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css")
                "/css/font-awesome.min.css")])

(defn loading-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app.js")]))

(defn cards-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app_devcards.js")]))

(defroutes
  routes
  (GET "/" [] (loading-page))
  (GET "/wip" [] (loading-page))
  (GET "/cards" [] (cards-page))
  (ANY "/elasticsearch/*" [] esproxy)
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
