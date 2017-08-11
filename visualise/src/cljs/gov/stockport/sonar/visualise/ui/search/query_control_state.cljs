(ns gov.stockport.sonar.visualise.ui.search.query-control-state)

(def query-types {:name          {:display-order        1
                                  :field-name  :name
                                  :field-type  :wildcard
                                  :description "Name"
                                  :placeholder "what name?"}
                  :address       {:display-order        2
                                  :field-name  :address
                                  :field-type  :address-with-postcode
                                  :description "Address"
                                  :placeholder "what address?"}
                  :postcode      {:display-order        3
                                  :field-name  :postcode
                                  :field-type  :postcode
                                  :description "Postcode"
                                  :placeholder "what postcode?"}
                  :dob           {:display-order        4
                                  :field-name  :dob
                                  :field-type  :date-of-birth
                                  :description "Date of birth"
                                  :placeholder "e.g.25/01/2016"}
                  :age-less-than {:display-order        5
                                  :field-name  :dob
                                  :field-type  :age-less-than
                                  :description "Aged up to"
                                  :placeholder "maximum age in years"}
                  :age-more-than {:display-order        6
                                  :field-name  :dob
                                  :field-type  :age-more-than
                                  :description "Aged over"
                                  :placeholder "minimum age in years"}
                  :all-fields    {:display-order        7
                                  :field-type  :query-all
                                  :description "All fields"
                                  :placeholder "enter general search"}
                  :event-source  {:display-order        8
                                  :field-name  :event-source
                                  :field-type  :match-text
                                  :description "Event Source"
                                  :placeholder "search for event source"}
                  :event-type    {:display-order        9
                                  :field-name  :event-type
                                  :field-type  :match-text
                                  :description "Event Type"
                                  :placeholder "search for event type"}})

(def options
  (map
    (fn [[query-type {:keys [description display-order]}]]
      {:target        query-type
       :description   description
       :display-order display-order})
    query-types))

(defn extract-query-defs [terms]
  (map (fn [{:keys [selected-control search-term]}]
         (assoc
           (select-keys (get query-types selected-control) [:field-type :field-name])
           :query search-term))
       terms))

