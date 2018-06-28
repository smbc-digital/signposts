(ns gov.stockport.sonar.visualise.ui.components.recent-updates
  (:require
    [gov.stockport.sonar.visualise.util.date :as d]
    [gov.stockport.sonar.visualise.util.fmt-help :as f]
    [reagent.core :as r]))

(defn recent-update [idx {:keys [event-source event-type last-updated]}]
  ^{:key (gensym)}
  [:div.col.col-2-sm.recent-update
   [:div.event-source-header
    [:h5  event-source " " event-type]]
   [:div.event-source-body
    [:h6  "Updated on"]
    [:p (d/date-format (d/parse-timestamp last-updated))]]])