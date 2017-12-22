(ns gov.stockport.sonar.visualise.ui.search.query-control-state
  (:require [gov.stockport.sonar.visualise.ui.search.search-controls :as sc])
  )

(def query-types {:name          {:display-order        1
                                  :field-name  :name
                                  :field-type  :wildcard
                                  :description "Name"
                                  :placeholder "what name?"
                                  :control sc/full-name
                                  }
                  :address       {:display-order        2
                                  :field-name  :address
                                  :field-type  :address-with-postcode
                                  :description "Address"
                                  :placeholder "what address?"
                                  :control sc/address
                                  }
                  :postcode      {:display-order        3
                                  :field-name  :postcode
                                  :field-type  :postcode
                                  :description "Postcode"
                                  :placeholder "what postcode?"
                                  :control sc/postcode}
                  :dob           {:display-order        4
                                  :field-name  :dob
                                  :field-type  :date-of-birth
                                  :description "Date of birth"
                                  :placeholder "e.g.25/01/2016"
                                  :control sc/dob}
                  :age-less-than {:display-order        5
                                  :field-name  :dob
                                  :field-type  :age-less-than
                                  :description "Aged up to"
                                  :placeholder "maximum age in years"
                                  :control sc/age-less-than}
                  :age-more-than {:display-order        6
                                  :field-name  :dob
                                  :field-type  :age-more-than
                                  :description "Aged over"
                                  :placeholder "minimum age in years"
                                  :control sc/age-more-than
                                  }
                  :all-fields    {:display-order        7
                                  :field-type  :query-all
                                  :description "All fields"
                                  :placeholder "enter general search"
                                  :control sc/all-fields
                                  }
                  :event-source  {:display-order        8
                                  :field-name  :event-source
                                  :field-type  :match-text
                                  :description "Event Source"
                                  :placeholder "search for event source"
                                  :control sc/event-source
                                  }
                  :event-type    {:display-order        9
                                  :field-name  :event-type
                                  :field-type  :match-text
                                  :description "Event Type"
                                  :placeholder "search for event type"
                                  :control sc/event-type
                                  }})

(def options
  (map
    (fn [[query-type {:keys [description display-order]}]]
      {:target        query-type
       :description   description
       :display-order display-order})
    query-types))

(defn extract-query-defs [terms]
  (map (fn [{:keys [query-type search-term]}]
         (assoc
           (select-keys (get query-types query-type) [:field-type :field-name])
           :query search-term))
       terms))

