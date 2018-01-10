(ns gov.stockport.sonar.visualise.ui.search.query-control
  (:require [reagent.core :as r]
            [reagent-forms.core :as rfc]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.ui.search.search-controls :as sc]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.state :refer [!search-options]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]))

(defn render-control[query-type]
  (get-in [query-type :control] qcs/options sc/all-fields ))


(defn render-search-option [query-type]
  [:div.col.col-md-3.search-event-item
   (render-control query-type)])

(defn search-options[count]
   [:span.search-option
   [:select.custom-select.form-control.mr-2
    {
      :on-change render-search-option
     }
    (map
      (fn [{:keys [target description]}]
        ^{:key target}
        [:option {:value target} description])
      (sort-by :display-order qcs/options))]])


(defn add-search-option []
  (js/alert "Here"))




(defn new-query-control[]
     [:div.container-fluid.py-1
     [:div.form-inline.row
      [:div.input-group {:id "search-options"}
          (search-options 1)
       [:i.fa.fa-plus{:on-click add-search-option}]
      [:span.input-group-btn
         [:button.btn.btn-success.mr-2
          {:type :submit :id "search-button"
           } "Search"]]]]])


