(ns gov.stockport.sonar.visualise.ui.components.welcome-status
  (:require [gov.stockport.sonar.visualise.state :refer [!status refresh-status!]]
            [gov.stockport.sonar.visualise.util.date :as d]
            [gov.stockport.sonar.visualise.util.fmt-help :as f]))

(defn row [{:keys [event-source event-type qty from to last-updated]}]
  [:tr
   [:td event-type]
   [:td (f/int-comma qty)]
   [:td (d/date-format (d/parse-timestamp from))]
   [:td (d/date-format (d/parse-timestamp to))]
   [:td event-source]
   [:td (d/human-since (d/parse-timestamp last-updated))]])

(defn welcome-message []
  (refresh-status!)
  (fn []
    [:div.container
     [:div.jumbotron.my-4
      [:h2 "You have access to all this data..."]
      [:hr.mr-4]
      [:table.table.table-striped
       [:thead
        [:tr
         [:th "Indicator"]
         [:th "Qty"]
         [:th "Earliest"]
         [:th "Latest"]
         [:th "Source"]
         [:th "Last refreshed"]]]
       `[:tbody
         ~@(map row (sort-by :event-type @!status))]]]]))
