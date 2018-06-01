(ns gov.stockport.sonar.visualise.ui.search.query-control-state
  (:require [gov.stockport.sonar.visualise.ui.search.search-controls :as sc])
  )

(def query-types {
                  :none          {
                                  :display-order 1
                                  :field-name :none
                                  :field-type :none
                                  :description "Select a category"
                                  :placeholder "none"
                                 }
                  :name          {:display-order 2
                                  :field-name  :name
                                  :field-type  :wildcard
                                  :description "NAME"
                                  :placeholder "what name?"
                                  :control sc/full-name
                                  }
                  :address       {:display-order 3
                                  :field-name  :address
                                  :field-type  :address-with-postcode
                                  :description "ADDRESS"
                                  :placeholder "what address?"
                                  :control sc/address
                                  }
                  :postcode      {:display-order   4
                                  :field-name  :postcode
                                  :field-type  :postcode
                                  :description "POSTCODE"
                                  :placeholder "what postcode?"
                                  :control sc/postcode}
                  :dob           {:display-order   5
                                  :field-name  :dob
                                  :field-type  :date-of-birth
                                  :description "DATE OF BIRTH"
                                  :placeholder "e.g.25/01/2016"
                                  :control sc/dob}
                  :age-less-than {:display-order        6
                                  :field-name  :dob
                                  :field-type  :age-less-than
                                  :description "AGE UP TO"
                                  :placeholder "maximum age in years"
                                  :control sc/age-less-than}
                  :age-more-than {:display-order        7
                                  :field-name  :dob
                                  :field-type  :age-more-than
                                  :description "AGE OVER"
                                  :placeholder "minimum age in years"
                                  :control sc/age-more-than
                                  }
                  :all-fields    {:display-order     8
                                  :field-type  :query-all
                                  :description "ALL"
                                  :placeholder "enter general search"
                                  :control sc/all-fields
                                  }
                  :event-source  {:display-order        9
                                  :field-name  :event-source
                                  :field-type  :match-text
                                  :description "EVENT SOURCE"
                                  :placeholder "search for event source"
                                  :control sc/event-source
                                  }
                  :event-type    {:display-order      10
                                  :field-name  :event-type
                                  :field-type  :match-text
                                  :description "EVENT TYPE"
                                  :placeholder "search for event type"
                                  :control sc/event-type
                                  }})

(def options
  (map
    (fn [[query-type {:keys [description display-order selected]}]]
      {:target        query-type
       :description   description
       :display-order display-order
       :selected    selected
       })
    query-types))

(defn extract-query-defs [terms]
  (map (fn [{:keys [query-type search-term]}]
         (assoc
           (select-keys (get query-types query-type) [:field-type :field-name])
           :query search-term))
       terms))

