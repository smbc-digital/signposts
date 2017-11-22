(ns gov.stockport.sonar.visualise.ui.pages.error-page
  (:require [reagent.core :as r]))


  (defn error_page [error!]
        [:div.login
        [busy/overlay]
        [:div.login-form
         [:h4 [:i.fa.fa-map-signs.mr-2] [:br] "Error"]
           [:p @error!]
         ]])


