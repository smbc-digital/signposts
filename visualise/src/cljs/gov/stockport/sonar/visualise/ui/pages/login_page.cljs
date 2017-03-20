(ns gov.stockport.sonar.visualise.ui.pages.login-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :as login]
            [gov.stockport.sonar.visualise.ui.login-form :refer [login-form]]))

(defn login-page []
  [:div.container-fluid.header
   [:div.row
    [:div.column.col-sm-3.stockport
     [:div.navbar-brand {:href "#"}
      [:img.logo
       {:alt "Stockport MBC",
        :src "/images/stockport_logo.gif"}]]]

    [:div.column.col-sm-9
     [:div.navbar-brand [:i.signpost.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]
      [:div.navbar-brand.title "SIGNPOSTS"]]]]

    [:div.row.body
      [:div.column.container-criteria.col-sm-3]
      [:div.column.col-sm-9
       [login-form]]
      ]])