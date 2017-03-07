(ns gov.stockport.sonar.visualise.ui.results.tabbed-results
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.results.raw-table :as rt]
            [gov.stockport.sonar.visualise.ui.results.timeline-flot :refer [timeline-flot]]))

(defn results-tabs [!data]
  (let [!selected (r/atom :timeline)]
    (fn []
      [:div
       [:ul.nav.nav-tabs
        [:li (if (= :timeline @!selected) {:class :active}) [:a.results-title {:on-click #(reset! !selected :timeline)} "TIMELINES"]]
        [:li (if (= :raw-data @!selected) {:class :active}) [:a.results-title {:on-click #(reset! !selected :raw-data)} "RAW DATA"]]]
       (if (= :timeline @!selected)
         [timeline-flot !data]
         [rt/raw-table !data])])))

