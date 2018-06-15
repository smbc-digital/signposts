(ns gov.stockport.sonar.visualise.ui.pages.login-page
  "Login Page of Signposts"
  (:require [gov.stockport.sonar.visualise.ui.login-form :refer [login-form]]
            [gov.stockport.sonar.visualise.state :refer [!app]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]))

(defn login-page []
  [:div.login
   [busy/overlay]
      [:div.login-form
     [:h4 [:i.fa.fa-map-signs.mr-2] [:br] "SIGNPOSTS"]
     [login-form]]])