(ns gov.stockport.sonar.visualise.ui.pages.login-page
  (:require [gov.stockport.sonar.visualise.ui.login-form :refer [login-form]]
            [gov.stockport.sonar.visualise.state :refer [!app]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]))

(defn login-page []
  [:div
   [busy/overlay]
   [:div.container-fluid.header
    [:div.row.stockport
     [:div.column.col-sm-2
      [:img {:src "/images/stockport_logo.gif" :alt "Stockport MBC"}]]
     [:div.column.col-sm-10.signposts
      [:div.navbar-brand [:i.signpost.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]
       [:div.navbar-brand.title "SIGNPOSTS"]]]]

    [:div.row.body
     [:div.column.container-criteria.col-sm-2]
     [:div.column.col-sm-10
      [login-form]]]]])