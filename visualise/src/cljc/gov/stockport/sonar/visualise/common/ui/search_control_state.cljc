(ns gov.stockport.sonar.visualise.common.ui.search-control-state)

(def initial-state {:available-fields [{:target      :name
                                        :field-name  :name
                                        :field-type  :match-text
                                        :description "Name"
                                        :placeholder "search for name"}
                                       {:target      :address
                                        :field-name  :address
                                        :field-type  :address-with-postcode
                                        :description "Address"
                                        :placeholder "search for address"}
                                       {:target      :age-less-than
                                        :field-name  :dob
                                        :field-type  :age-less-than
                                        :description "Aged up to"
                                        :placeholder "enter maximum age in years"}
                                       {:target      :age-more-than
                                        :field-name  :dob
                                        :field-type  :age-more-than
                                        :description "Aged over"
                                        :placeholder "enter minimum age in years"}
                                       {:target      :all-fields
                                        :field-type  :query-all
                                        :description "All fields"
                                        :placeholder "enter general search"}
                                       {:target      :event-source
                                        :field-name  :event-source
                                        :field-type  :match-text
                                        :description "Event Source"
                                        :placeholder "search for event source"}
                                       {:target      :event-type
                                        :field-name  :event-type
                                        :field-type  :match-text
                                        :description "Event Type"
                                        :placeholder "search for event type"}
                                       ]})

(defn path [control-id & extras]
  `[:controls ~control-id ~@extras])

(defn add-search-control [!state control-id]
  (swap! !state assoc-in (path control-id) initial-state))

(defn remove-search-control [!state control-id]
  (swap! !state update-in (drop-last (path control-id)) dissoc control-id))

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
  (let [{:keys [target default]} (first (available-fields !state control-id))
        mk-get (partial mk-search-criteria-get !state control-id sc-id)
        mk-set (partial mk-search-criteria-set !state control-id sc-id)
        get-selected-field (mk-get :target)
        selected-field-def #(field-def !state control-id (get-selected-field))]
    {:id                 sc-id
     :target             target
     :query              default
     :set-selected-field (mk-set keyword :target)
     :get-selected-field get-selected-field
     :set-query          (mk-set identity :query)
     :get-query          (mk-get :query)
     :get-field-name     #(:field-name (selected-field-def))
     :get-field-type     #(:field-type (selected-field-def))
     :get-placeholder    #(:placeholder (selected-field-def))
     :on-remove          (mk-remove-search-criteria !state control-id sc-id)}))

(defn add-search-criteria [!state control-id]
  (swap! !state update-in (path control-id :search-criteria)
         (fn [m]
           (let [sc-id (gensym)]
             (assoc m sc-id (create-search-criteria !state control-id sc-id))))))

(defn get-all-search-criteria [!state control-id]
  (sort-by :id (vals (get-in @!state (path control-id :search-criteria)))))

(defn init-search-control [!state control-id]
  (add-search-control !state control-id)
  (add-search-criteria !state control-id))
