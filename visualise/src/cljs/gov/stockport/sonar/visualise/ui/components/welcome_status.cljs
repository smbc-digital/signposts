(ns gov.stockport.sonar.visualise.ui.components.welcome-status
  (:require [gov.stockport.sonar.visualise.state :refer [!status refresh-status!]]
            [gov.stockport.sonar.visualise.util.date :as d]
            [gov.stockport.sonar.visualise.util.fmt-help :as f]))

(defn row [idx {:keys [event-source event-type qty from to last-updated]}]
  [:tr
   [:th (when (= idx 0) event-source)]
   [:td event-type]
   [:td.text-right (f/int-comma qty)]
   [:td.text-center (d/date-format (d/parse-timestamp from))]
   [:td.text-center (d/date-format (d/parse-timestamp to))]
   [:td.text-center (d/human-since (d/parse-timestamp last-updated))]])

(defn source [[_ events]]
  `(~@(map-indexed row (sort-by :event-type events))))

(defn welcome-message []
  (refresh-status!)
  (fn []
    [:div.container
     [:div.jumbotron.my-4
      [:h4 "You have access to the following records"]
      [:hr]
      [:table.table.table-striped
       [:thead
        [:tr
         [:th {:colSpan 2} "Record Source & Type"]
         [:th.text-right "Quantity"]
         [:th.text-center "From"]
         [:th.text-center "To"]
         [:th.text-center "Last refreshed"]]]
       `[:tbody
         ~@(map source (sort (group-by :event-source @!status)))]]]]))
