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

(defn add-search-criteria!
  ([!state]
   (when (not (str/blank? (search-term !state)))
     (apply add-search-criteria! !state (vals (select-keys @!state [:selected-control :search-term])))))
  ([!state query-type search-term]
   (when (not (str/blank? search-term))
     (let [new-criteria {:selected-control query-type :search-term search-term}]
       (callback (swap! !state
                        (fn [state]
                          (-> state
                              (update :criteria
                                      (fn [existing-criteria]
                                        (if (contains-criteria? existing-criteria new-criteria)
                                          (replace-criteria existing-criteria new-criteria)
                                          (concat existing-criteria [new-criteria]))))
                              (assoc :search-term "")))))))))

(defn remove-search-criteria! [!state query-type]
  (callback (swap! !state update :criteria
                   (fn [search-criteria]
                     (filter (fn [{:keys [selected-control]}]
                               (not (= query-type selected-control))) search-criteria)))))


(defn search-criteria [!state]
  (:criteria @!state))