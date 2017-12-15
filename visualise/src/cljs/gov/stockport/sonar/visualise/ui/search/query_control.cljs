(ns gov.stockport.sonar.visualise.ui.search.query-control
  (:require [reagent.core :as r]
  [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
  [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
   [gov.stockport.sonar.visualise.ui.search.search-controls :as sc]
  [gov.stockport.sonar.visualise.query.client :refer [search]]
  [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]))

(defn render-control[query-type]
  (get-in [query-type :control] qcs/options sc/all-fields )
  )

(defn search-options[]
   [div.search=options
   [:select
    {:on-change render-control}
    ]]
  )

(defn query-control[]
    [:div.query-control
      [search-options]

     ]

  )
