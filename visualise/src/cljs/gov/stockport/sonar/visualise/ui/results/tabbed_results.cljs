(ns gov.stockport.sonar.visualise.ui.results.tabbed-results
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.results.raw-table :as rt]
            [gov.stockport.sonar.visualise.ui.results.timeline-flot :refer [timeline-flot]]))

(defn results-tabs [!data]
  (let [!selected (r/atom :timeline)]
    (fn []
      [:div
       [:div.nav.nav-tabs
        [:li.nav-item
         [:span.nav-link {:on-click #(reset! !selected :timeline)
                          :class    (if (= :timeline @!selected) :active "")} "TIMELINES"]]
        [:li.nav-item
         [:span.nav-link {:on-click #(reset! !selected :raw-data)
                          :class    (if (= :raw-data @!selected) :active "")} "RAW DATA"]]]
       (if (= :timeline @!selected)
         [timeline-flot !data]
         [rt/raw-table !data])])))