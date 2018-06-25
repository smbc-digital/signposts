(ns gov.stockport.sonar.visualise.ui.search.query-control-state
  "Stores query control state"
  )

(def query-types
  {
   :none {:display-order 1
          :field-name :none
          :field-type :none
          :description "Select a category"
          :placeholder "none"}

   :name  {:display-order 2
           :field-name  :name
           :field-type  :wildcard
           :description "NAME"
           :placeholder "what name?"
           :type "text"
           :size "1"}

   :address  {:display-order 3
              :field-name  :address
              :field-type  :address-with-postcode
              :description "ADDRESS"
              :placeholder "what address?"
              :type "text"
              :size "10"}

  :postcode   {:display-order   4
               :field-name  :postcode
               :field-type  :postcode
               :description "POSTCODE"
               :placeholder "what postcode?"
               :type "text"
               :size "8"}

  :dob         { :display-order   5
                 :field-name  :dob
                 :field-type  :date-of-birth
                 :description "DATE OF BIRTH"
                 :placeholder "DOB e.g.25/01/2016"
                 :type "date"
                 :size "8"}

  :age-less-than {:display-order        6
                  :field-name  :dob
                  :field-type  :age-less-than
                  :description "AGE UP TO"
                  :placeholder "maximum age in years"
                  :type "number"
                  :size "3"}

  :age-more-than {:display-order        7
                  :field-name  :dob
                  :field-type  :age-more-than
                  :description "AGE OVER"
                  :placeholder "minimum age in years"
                  :type "number"
                  :size "3"}

  :age-range  {:display-order 11
                :field-name :dob
                :field-type [:age-more-than :age-less-than]
                :description "AGE RANGE"
                :placeholder "Age from to"
                :size 3
                }

  :all-fields    {:display-order     8
                  :field-type  :query-all
                  :description "ALL"
                  :placeholder "enter general search"
                  :type "text"
                  :size "10"}

  :event-source  {:display-order        9
                  :field-name  :event-source
                  :field-type  :match-text
                  :description "EVENT SOURCE"
                  :placeholder "search for event source"
                  :type "text"
                  :size "10"}

  :event-type    {:display-order      10
                  :field-name  :event-type
                  :field-type  :match-text
                  :description "EVENT TYPE"
                  :placeholder "search for event type"
                  :type "text"
                  :size "10"
                  }})

(def options
  (map
    (fn [
       [query-type {:keys [description display-order selected]}]]
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

