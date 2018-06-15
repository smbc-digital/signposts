(ns gov.stockport.sonar.visualise.ui.busy
  (:require [gov.stockport.sonar.visualise.state :refer [!app]]))

(defn overlay []
  "Shows Spinner over translucant backgorund"
  (when (:ajax-in-progress @!app)
    [:div.busy
     [:div.spinner
      [:i.fa.fa-spin.fa-refresh.fa-5x]]]))
