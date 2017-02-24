(ns visualise.common.results.individuals-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.results.individuals :as i]))

(def some-data [{:name       "Jim Zelda"
                 :dob        1
                 :address    "Brummie"
                 :event-data "baltis"}
                {:name       "Jim Zelda"
                 :dob        1
                 :address    "Brummie"
                 :event-data "weird accent"}
                {:name       "Zeshan Aardvark"
                 :dob        1
                 :address    "Cockney"
                 :event-data "jellied eels"}
                {:name       "Richard Sarky"
                 :dob        1
                 :address    "Whippets"
                 :event-data "flat caps"}])

(fact "should group data into individuals by name, dob and address"
      (i/individuals some-data) => [{:name    "Zeshan Aardvark"
                                     :dob     1
                                     :address "Cockney"}
                                    {:name    "Richard Sarky"
                                     :dob     1
                                     :address "Whippets"}{:name    "Jim Zelda"
                                     :dob     1
                                     :address "Brummie"}])
