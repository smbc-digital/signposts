(ns gov.stockport.sonar.visualise.ui.search.search-control-state
  (:require [clojure.string :as str]))

(defn init! [!state on-change-callback]
  (reset! !state {:selected-control   :name
                  :query              ""
                  :on-change-callback on-change-callback
                  :criteria           []}))

(defn set-selected-field! [!state field]
  (swap! !state assoc :selected-control field))

(defn selected-control [!state]
  (:selected-control @!state))

(defn search-term [!state]
  (:search-term @!state))

(defn set-search-term! [!state search-term]
  (swap! !state assoc :search-term search-term))

(defn- callback [{:keys [on-change-callback criteria]}]
  (on-change-callback criteria))

(defn- replace-criteria [existing-criteria new-criteria]
  (map
    (fn [criteria]
      (if (= (:selected-control criteria) (:selected-control new-criteria))
        new-criteria
        criteria))
    existing-criteria))

(defn- contains-criteria? [existing-criteria {:keys [selected-control]}]
  (some
    (fn [criteria]
      (= (:selected-control criteria) selected-control))
    existing-criteria))

(defn add-search-criteria! [!state]
  (when (not (str/blank? (search-term !state)))
    (callback (swap! !state
                     (fn [state]
                       (-> state
                           (update :criteria
                                   (fn [existing-criteria]
                                     (let [new-criteria (select-keys state [:selected-control :search-term])]
                                       (if (contains-criteria? existing-criteria new-criteria)
                                         (replace-criteria existing-criteria new-criteria)
                                         (concat existing-criteria [new-criteria])))))
                           (assoc :search-term "")))))))

(defn remove-search-criteria! [!state criteria-to-remove]
  (callback (swap! !state update :criteria
                   (fn [search-criteria]
                     (filter (fn [existing-criteria]
                               (not (= criteria-to-remove existing-criteria))) search-criteria)))))

(defn search-criteria [!state]
  (:criteria @!state))
