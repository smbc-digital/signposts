(ns visualise.common.ui.search-control-state
  (:require [visualise.common.query.base :as qb]))

(def initial-state {:available-fields [{:target      :name
                                        :type        :match-text
                                        :description "Name"
                                        :placeholder "search for name"}
                                       {:target      :address
                                        :type        :match-text
                                        :description "Address"
                                        :placeholder "search for address"}
                                       {:target      :dob
                                        :type        :match-text
                                        :description "Date of Birth"
                                        :placeholder "search by date of birth"}
                                       {:target      :event-source
                                        :type        :match-text
                                        :description "Event Source"
                                        :placeholder "search for event source"}
                                       {:target      :event-type
                                        :type        :match-text
                                        :description "Event Type"
                                        :placeholder "search for event type"}
                                       ]})

(defn path [control-id & extras]
  `[:controls :search-controls ~control-id ~@extras])

(defn add-search-control [!state control-id]
  (swap! !state assoc-in (path control-id) initial-state))

(defn remove-search-control [!state control-id]
  (swap! !state update-in (drop-last (path control-id)) dissoc control-id))

(defn reset-search-control [!state control-id]
  (swap! !state assoc-in (path control-id) initial-state))

(defn available-fields [!state control-id]
  (get-in @!state (path control-id :available-fields)))

(defn field-def [!state control-id k]
  (first (filter #(= (:target %) k) (available-fields !state control-id))))

(defn remove-search-critieria [!state control-id sc-id]
  (swap! !state update-in (path control-id :search-criteria) dissoc sc-id))

(defn mk-remove-search-criteria [!state control-id sc-id]
  (fn [] (remove-search-critieria !state control-id sc-id)))

(defn mk-search-criteria-set [!state control-id sc-id keywordise k]
  (fn [val] (swap! !state assoc-in (path control-id :search-criteria sc-id k) (keywordise val))))

(defn mk-search-criteria-get [!state control-id sc-id k]
  (fn [] (get-in @!state (path control-id :search-criteria sc-id k))))

(defn create-search-criteria [!state control-id sc-id]
  (let [{:keys [target default type]} (first (available-fields !state control-id))
        mk-get (partial mk-search-criteria-get !state control-id sc-id)
        mk-set (partial mk-search-criteria-set !state control-id sc-id)
        get-selected-field (mk-get :target)]
    {:id                 sc-id
     :target             target
     :query              default
     :type               type
     :set-selected-field (mk-set keyword :target)
     :get-selected-field get-selected-field
     :set-query          (mk-set identity :query)
     :get-query          (mk-get :query)
     :get-placeholder    #(:placeholder (field-def !state control-id (get-selected-field)))
     :on-remove          (mk-remove-search-criteria !state control-id sc-id)}))

(defn add-search-criteria [!state control-id]
  (swap! !state update-in (path control-id :search-criteria)
         (fn [m]
           (let [sc-id (keyword (gensym "search-criteria-"))]
             (assoc m sc-id (create-search-criteria !state control-id sc-id))))))

(defn get-all-search-criteria [!state control-id]
  (sort-by :id (vals (get-in @!state (path control-id :search-criteria)))))

(defn init-search-control [!state control-id]
  (add-search-control !state control-id)
  (add-search-criteria !state control-id))

(defn query-terms [!state control-id]
  (map
    (fn [{:keys [get-selected-field get-query]}]
      [(get-selected-field) (get-query)])
    (get-all-search-criteria !state control-id)))

(defn extract-query [!state control-id]
  (reduce
    (fn [qip [term value]]
      (qb/with-match qip term value))
    (-> (qb/query)
        (qb/with-size 25))
    (query-terms !state control-id)))

