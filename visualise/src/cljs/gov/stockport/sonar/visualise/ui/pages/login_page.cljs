(ns gov.stockport.sonar.visualise.ui.pages.login-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :as login]))

(defn login-page []
  [:div.container-fluid.header
   [:div.row
    [:div.column.col-sm-3.stockport
     [:a.navbar-brand {:href "#"}
      [:img.logo
       {:alt "Stockport MBC",
        :src "/images/stockport_logo.gif"}]]]

    [:div.column.col-sm-9
     [:a.navbar-brand [:i.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]]
     [:a.navbar-brand.title {:href "#"} "SIGNPOSTS - Login"]]

    [:div.row.body
     [:div.column.col-sm-9
      [:button {:on-click #(login/attempt-login "elastic" "changeme")} "Login"]]
     ]]])