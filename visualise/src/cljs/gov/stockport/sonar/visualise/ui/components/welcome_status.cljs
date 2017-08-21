(ns gov.stockport.sonar.visualise.ui.components.welcome-status
  (:require [gov.stockport.sonar.visualise.state :refer [!status refresh-status!]]
            [gov.stockport.sonar.visualise.util.date :as d]
            [gov.stockport.sonar.visualise.util.fmt-help :as f]))


(defn row [{:keys [event-type qty from to last-updated]}]
  ^{:key (gensym)}
  [:tr
   [:td event-type]
   [:td.text-right (f/int-comma qty)]
   [:td.text-center (d/date-format (d/parse-timestamp from))]
   [:td.text-center (d/date-format (d/parse-timestamp to))]
   [:td.text-center (d/human-since (d/parse-timestamp last-updated))]])

(defn source [[event-source events]]
  `(^{:key (gensym)}
  [:tr
   [:th ~event-source]
   [:th.text-right "Quantity"]
   [:th.text-center "From"]
   [:th.text-center "To"]
   [:th.text-center "Last refreshed"]
   ]
     ~@(map row (sort-by :event-type events))))


(defn welcome-message []
  (refresh-status!)
  (fn []
    [:div.container
     [:div.jumbotron.my-4
      [:h4 "You have access to the following records"]
      [:hr]
      [:table.table.table-striped
       `[:tbody
         ~@(map source (group-by :event-source @!status))]]]]))
