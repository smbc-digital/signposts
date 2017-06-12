(ns gov.stockport.sonar.visualise.ui.search.query-control-state)

(def query-types {:name          {:sort        1
                                  :field-name  :name
                                  :field-type  :match-text
                                  :description "Name"
                                  :placeholder "what name?"}
                  :address       {:sort        2
                                  :field-name  :address
                                  :field-type  :address-with-postcode
                                  :description "Address"
                                  :placeholder "what address?"}
                  :postcode      {:sort        3
                                  :field-name  :postcode
                                  :field-type  :postcode
                                  :description "Postcode"
                                  :placeholder "what postcode?"}
                  :age-less-than {:sort        4
                                  :field-name  :dob
                                  :field-type  :age-less-than
                                  :description "Aged up to"
                                  :placeholder "maximum age in years"}
                  :age-more-than {:sort        5
                                  :field-name  :dob
                                  :field-type  :age-more-than
                                  :description "Aged over"
                                  :placeholder "minimum age in years"}
                  :all-fields    {:sort        6
                                  :field-type  :query-all
                                  :description "All fields"
                                  :placeholder "enter general search"}
                  :event-source  {:sort        7
                                  :field-name  :event-source
                                  :field-type  :match-text
                                  :description "Event Source"
                                  :placeholder "search for event source"}
                  :event-type    {:sort        8
                                  :field-name  :event-type
                                  :field-type  :match-text
                                  :description "Event Type"
                                  :placeholder "search for event type"}})

(def options
  (doall
    (map
      (fn [[query-type {:keys [description]}]]
        {:target      query-type
         :description description})
      query-types)))

(defn extract-query-defs [terms]
  (map (fn [{:keys [selected-control search-term]}]
         (assoc
           (select-keys (get query-types selected-control) [:field-type :field-name])
           :query search-term))
       terms))

