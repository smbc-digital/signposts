(ns gov.stockport.sonar.visualise.ui.pages.error-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as nsc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!app !data]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state]]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.ui.components.welcome-status :refer [welcome-message]]))


  (defn error_page [error!]
        [:div.login
        [busy/overlay]
        [:div.login-form
         [:h4 [:i.fa.fa-map-signs.mr-2] [:br] "Error"]
           [:p @error!]
         ]])


