(ns gov.stockport.sonar.visualise.ui.search.search-history
  (:require [gov.stockport.sonar.visualise.state :refer [!search-control-state !search-history !data]]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :refer [query-types]]))

(defn- to-query-item[{:keys [query-type search-term]}]
           (let [query-item {}]
             (-> query-item
             (assoc  :field-type (get-in query-types [query-type :field-type] ))
             (assoc  :field-name (get-in query-types [query-type :field-name] ))
             (assoc  :query search-term))))


(defn query-from-search-control-state [search-history-item]
     (map to-query-item search-history-item)
  )

(defn stored-search-criteria[index]
  (let [search-history-item (nth @!search-history index)]
  (swap! !search-control-state assoc :criteria  search-history-item)
  (search  (query-from-search-control-state search-history-item) (h/default-handler !data))))

(defn add-search-history![]
  (if (not (empty?(:criteria @!search-control-state)))
    (do
      (if (< 10 (count @!search-history))
        (reset! !search-history (drop-last @!search-history)))
        (swap! !search-history conj (:criteria @!search-control-state)))))

(defn clear-all-searches![]
  (reset! !search-history '()))


