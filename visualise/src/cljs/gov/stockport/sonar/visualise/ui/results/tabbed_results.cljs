(ns gov.stockport.sonar.visualise.ui.results.tabbed-results
  "Displays Contact History and Events Graph"
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.contacts :refer [contact-history]]
            [gov.stockport.sonar.visualise.ui.results.timeline-flot :refer [timeline-flot]]))

(defn results-tab [!data]
  "Shows graph and contact history from returned data"
  (let [!selected (r/atom :timeline)]
    (fn []
      [:div
       [timeline-flot !data]
       [contact-history !data]])))


