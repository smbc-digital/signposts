(ns gov.stockport.sonar.visualise.ui.components.welcome-status
  (:require [gov.stockport.sonar.visualise.state :refer [!status refresh-status!]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [clear-all-searches!]]
            [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.components.recent-searches :refer [search-history]]
            [gov.stockport.sonar.visualise.ui.components.recent-updates :refer [recent-update]]
            ))


(defn welcome-message []
  (refresh-status!)
  ^{:key (gensym)}
  [:div {:id "welcome-message"}
     [:div.col-md-12.recent-searches
      [:div.container
      [:h4  "RECENT SEARCHES" "  "
       [:span  {:style {:font-size "15px" :text-decoration "underline" :margin-left "30px"}}
                [:a {:on-click #(clear-all-searches!)}  " Clear all searches"]]]
       [:div
        [:div.row.col-12.recent-searches
         (search-history)
         ]]]]
     [:div.col-md-12.recent-updates
      [:div.container
      [:h4 "RECENT UPDATES"]
       `[:div
         [:div.row.col-12
          (~@(map-indexed recent-update (take 4 (reverse(sort-by :last-updated  @!status)))))]
        ]]]])