(ns gov.stockport.sonar.visualise.ui.search.search-history
  (:require [gov.stockport.sonar.visualise.state :refer [!search-control-state !search-history]]))

(defn stored-search-criteria[]
  (js/alert "Here")
  (assoc! :criteria !search-control-state (:first 1 @!search-history)))

(defn add-search-history![]
  (if (not (empty?(:criteria @!search-control-state)))
    (do
      (if (< 10 (count @!search-history))
        (reset! !search-history (drop-last @!search-history)))
        (swap! !search-history conj (:criteria @!search-control-state)))))

(defn clear-all-searches![]
  (reset! !search-history '()))