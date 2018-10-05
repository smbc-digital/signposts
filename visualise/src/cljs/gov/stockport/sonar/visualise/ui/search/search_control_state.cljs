(ns gov.stockport.sonar.visualise.ui.search.search-control-state
  "Search Control State"
  (:require [clojure.string :as str]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state !search-history]]))

(defn init! [on-change-callback]
  (reset! !search-control-state {:selected-control   :name
                                 :query              ""
                                 :on-change-callback on-change-callback
                                 :criteria           []}))

(defn set-selected-field! [field]
  (if (not= field "")
  (swap! !search-control-state assoc :selected-control field)))

(defn selected-control []
  (:selected-control @!search-control-state))

(defn search-term []
  (:search-term @!search-control-state))

(defn set-search-term! [search-term]
  (swap! !search-control-state assoc :search-term search-term))

(defn- callback [{:keys [on-change-callback criteria]}]
  (on-change-callback criteria))

(defn- replace-criteria [existing-criteria new-criteria]
  (map
    (fn [criteria]
      (if (= (:query-type criteria) (:query-type new-criteria))
        new-criteria
        criteria))
    existing-criteria))

(defn- contains-criteria? [existing-criteria {:keys [query-type]}]
  (some
    (fn [criteria]
      (= (:query-type criteria) query-type))
    existing-criteria))

(defn- swap-criteria!
  [new-criteria]
  (swap! !search-control-state
         (fn [state]
           (-> state
               (update :criteria
                       (fn [existing-criteria]
                         (if (contains-criteria? existing-criteria new-criteria)
                           (replace-criteria existing-criteria new-criteria)
                           (concat existing-criteria [new-criteria]))))
               (assoc :search-term "")))))

(defn add-search-criteria!
  ([]
   (when (not (str/blank? (search-term)))
     (apply add-search-criteria! (vals (select-keys @!search-control-state [:selected-control :search-term])))))
  ([query-type search-term]
   (when (not (str/blank? search-term))
     (let [new-criteria {:query-type query-type :search-term search-term}]
       (swap-criteria! new-criteria)
       ))))

(defn add-search-criteria-and-search!
  ([]
   (js/console.log (pr-str "status" (:criteria @!search-control-state)))
   (when (not (str/blank? (search-term)))
     (apply add-search-criteria-and-search! (vals (select-keys @!search-control-state [:selected-control :search-term])))))
  ([query-type search-term]
   (when (not (str/blank? search-term))
     (let [new-criteria {:query-type query-type :search-term search-term}]
       (callback (swap-criteria! new-criteria))
       ))))

(defn remove-search-criteria! [query-type-to-remove]
  (swap! !search-control-state update :criteria
                   (fn [search-criteria]
                     (filter (fn [{:keys [query-type]}]
                               (not (= query-type-to-remove query-type))) search-criteria))))

(defn search-criteria []
  (:criteria @!search-control-state))