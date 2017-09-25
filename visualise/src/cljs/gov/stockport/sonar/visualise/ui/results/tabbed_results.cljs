(ns gov.stockport.sonar.visualise.ui.results.tabbed-results
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.raw-table :as rt]
            [gov.stockport.sonar.visualise.ui.results.contacts :as con]
            [gov.stockport.sonar.visualise.ui.results.timeline-flot :refer [timeline-flot]]))





(defn results-tab [!data]
  (let [!selected (r/atom :timeline)]
    (fn []
      [:div
       [timeline-flot !data]
       [con/contact-history !data]
         ])))


