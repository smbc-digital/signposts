(ns gov.stockport.sonar.visualise.ui.pages.home-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as sc]
            [gov.stockport.sonar.visualise.ui.search.new-search-control :as nsc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!app !data]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]))

(defn results [!data]
    [:div.row.body
     [:div.column.container-results.col-sm-3
      [:div.column-title.results-title "RESULTS"]
      [ic/cards !data]]
     [:div.column.container-timeline.col-sm-9
      [tr/results-tabs !data]]])

(defn summary []
  [:div.row.body
   [:div.column.container-results.col-sm-12
    [:div "Welcome to Signposts"]
    [:div "Please search for something to get started"]]])

(defn home-page []
  [:div
   [busy/overlay]
   [:div.container-fluid.header
    [:div.row.stockport
     [:div.column.col-sm-12.signposts
      [:div.navbar-brand [:i.signpost.fa.fa-map-signs.pull-left.fa-2x.fa-align-center {:aria-hidden "true"}]
       [:div.navbar-brand.title "SIGNPOSTS"]]
      [:div.form
       [:button.logout.btn.btn-primary.pull-right {:type :submit :on-click ac/logout} "Logout"]]]]

    [:div.row.search-bar
     [nsc/new-search-control (h/default-handler !data)]]

    (if (not-empty (:result @!data))
      [results !data]
      [summary])]])