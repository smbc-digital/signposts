(ns gov.stockport.sonar.visualise.ui.search.search-control-state
  (:require [clojure.string :as str]))

(defn init! [!state on-change-callback]
  (swap! !state assoc :search-control {:selected-control   :name
                                       :on-change-callback on-change-callback
                                       :criteria           []}))

(defn set-selected-field! [!state field]
  (swap! !state assoc-in [:search-control :selected-control] field))

(defn selected-control [!state]
  (get-in @!state [:search-control :selected-control]))

(defn search-term [!state]
  (get-in @!state [:search-control :search-term]))

(defn set-search-term! [!state search-term]
  (swap! !state assoc-in [:search-control :search-term] search-term))

(defn- callback [{{:keys [on-change-callback criteria]} :search-control}]
  (on-change-callback criteria))

(defn add-search-criteria! [!state]
  (when (not (str/blank? (search-term !state)))
    (callback (swap! !state
                     (fn [{:keys [search-control] :as state}]
                       (-> state
                           (update-in
                             [:search-control :criteria]
                             (fn [criteria]
                               (concat criteria
                                       [(select-keys search-control [:selected-control :search-term])])))
                           (assoc-in [:search-control :search-term] "")))))))

(defn remove-search-criteria! [!state criteria-to-remove]
  (callback (swap! !state update-in [:search-control :criteria]
                   (fn [search-criteria]
                     (filter (fn [existing-criteria]
                               (not (= criteria-to-remove existing-criteria))) search-criteria)))))

(defn search-criteria [!state]
  (get-in @!state [:search-control :criteria]))