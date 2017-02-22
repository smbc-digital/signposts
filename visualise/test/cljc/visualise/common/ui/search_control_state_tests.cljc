(ns visualise.common.ui.search-control-state-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.ui.search-control-state :as s]))

(fact "should add control with initial search criteria"
      (let [!state (atom {})
            _ (s/init-search-control !state :control-id)]
        (get-in @!state [:controls :control-id]) =not=> nil
        (keys (get-in @!state [:controls :control-id])) => (contains [:available-fields :search-criteria])
        (count (get-in @!state [:controls :control-id :search-criteria])) => 1))

(fact "should return all search critiera"
      (let [!state (atom {})
            _ (s/init-search-control !state :control-id)
            _ (s/add-search-criteria !state :control-id)]
        (count (get-in @!state [:controls :control-id :search-criteria])) => 2
        (vals (get-in @!state [:controls :control-id :search-criteria])) => (s/get-all-search-criteria !state :control-id)))

(fact "should return specific field definitions"
      (let [!state (atom {})
            _ (s/init-search-control !state :control-id)]
        (s/field-def !state :control-id :name) => (first (s/available-fields !state :control-id))))

(fact "about each search criteria"
      (let [!state (atom {})
            _ (s/init-search-control !state :control-id)
            available-fields (s/available-fields !state :control-id)
            a-search-criteria (first (s/get-all-search-criteria !state :control-id))]

        (fact "the selected field is defaulted to the first available field"
              (let [{:keys [get-selected-field]} a-search-criteria]
                (get-selected-field) => (:target (first available-fields))))

        (fact "they provide functions to modify the selected-field"
              (let [{:keys [get-selected-field set-selected-field]} a-search-criteria]
                (get-selected-field) => :name
                (set-selected-field :address)
                (get-selected-field) => :address))

        (fact "modifying the selected field, modifies the placeholder and field type"
              (let [{:keys [get-placeholder get-field-type set-selected-field]} a-search-criteria]
                (set-selected-field :name)
                (get-placeholder) => (:placeholder (s/field-def !state :control-id :name))
                (get-field-type) => (:field-type (s/field-def !state :control-id :name))
                (set-selected-field :dob)
                (get-placeholder) => (:placeholder (s/field-def !state :control-id :dob))
                (get-field-type) => (:field-type (s/field-def !state :control-id :dob))))

        (fact "they provide functions to modify the user supplied query"
              (let [{:keys [get-query set-query]} a-search-criteria]
                (get-query) => nil
                (set-query "some-value")
                (get-query) => "some-value"))

        (fact "they provide a function to remove themselves"
              (let [{:keys [on-remove]} a-search-criteria]
                (count (s/get-all-search-criteria !state :control-id)) => 1
                (on-remove)
                (count (s/get-all-search-criteria !state :control-id)) => 0))))