(ns gov.stockport.sonar.visualise.common.scratch)

(def !a (atom {}))

(def initial-state {:available-fields [{:target      :name
                                        :type        :match-text
                                        :description "Name"
                                        :default     ""
                                        :placeholder "enter name"}
                                       {:target      :address
                                        :type        :match-text
                                        :description "Address"
                                        :default     "enter address"}]})

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

(defn remove-search-critieria [!state control-id sc-id]
  (swap! !state update-in (path control-id) dissoc sc-id))

(defn mk-remove-search-criteria [!state control-id sc-id]
  (fn [] (remove-search-critieria !state control-id sc-id)))

(defn mk-search-criteria-set [!state control-id sc-id k]
  (fn [val] (swap! !state assoc-in (path control-id sc-id k) val)))

(defn mk-search-criteria-get [!state control-id sc-id k]
  (fn [] (get-in @!state (path control-id sc-id k))))

(defn create-search-criteria [!state control-id sc-id]
  (let [{:keys [target default type]} (first (available-fields !state control-id))
        mk-get (partial mk-search-criteria-get !state control-id sc-id)
        mk-set (partial mk-search-criteria-set !state control-id sc-id)]
    {:target             target
     :query              default
     :type               type
     :set-selected-field (mk-set :target)
     :get-selected-field (mk-get :target)
     :set-query          (mk-set :query)
     :get-query          (mk-get :query)
     :on-remove          (mk-remove-search-criteria !state control-id sc-id)}))

(defn add-search-criteria [!state control-id]
  (swap! !state update-in (path control-id :search-criteria)
         (fn [m]
           (let [sc-id (keyword (gensym "search-criteria-"))]
             (assoc m sc-id (create-search-criteria !state control-id sc-id))))))

(defn get-search-criteria [!state control-id]
  (vals (get-in @!state (path control-id :search-criteria))))

(defn init-search-control [!state control-id]
  (add-search-control !state control-id)
  (add-search-criteria !state control-id))

(add-search-control !a :sc)

(reset-search-control !a :sc)

(add-search-criteria !a :sc)

(add-search-criteria !a :sc)



