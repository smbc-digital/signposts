(ns gov.stockport.sonar.visualise.ui.components.welcome-status
  (:require [gov.stockport.sonar.visualise.state :refer [!status refresh-status! !search-history !search-control-state]]
            [gov.stockport.sonar.visualise.util.date :as d]
            [gov.stockport.sonar.visualise.util.fmt-help :as f]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.ui.search.search-history :as sh]
            [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.components.recent-searches :refer [search-history]]
            [gov.stockport.sonar.visualise.ui.components.recent-updates :refer [recent-update]]
            ))


(defn welcome-message []
  (refresh-status!)
  [:div.container
     [:div.col-md-12.recent-searches
      [:h4  "RECENT SEARCHES" "  "
       [:span  {:style {:font-size "15px" :text-decoration "underline"}}
                [:a {:onClick sh/clear-all-searches!}  " Clear all searches"]]]
       [:div
        [:div.row.col-12.recent-searches
         (search-history)
         ]]]

     [:div.col-md-12.recent-updates
      [:h4 "RECENT UPDATES"]
       `[:div
         [:div.row.col-12
          (~@(map-indexed recent-update (take 4 (reverse(sort-by :last-updated  @!status)))))]
        ]]])