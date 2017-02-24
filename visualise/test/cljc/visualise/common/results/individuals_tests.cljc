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
                 :dob        2
                 :address    "Cockney"
                 :event-data "jellied eels"}
                {:name       "Richard Sarky"
                 :dob        3
                 :address    "Whippets"
                 :event-data "flat caps"}])

(fact "should group data into individuals by name, dob and address, ordered by surname"
      (i/individuals some-data) => [{:idx     0
                                     :ikey    {:name    "Zeshan Aardvark"
                                               :dob     2
                                               :address "Cockney"}
                                     :name    "Zeshan Aardvark"
                                     :dob     2
                                     :address "Cockney"}
                                    {:idx     1
                                     :ikey    {:name    "Richard Sarky"
                                               :dob     3
                                               :address "Whippets"}
                                     :name    "Richard Sarky"
                                     :dob     3
                                     :address "Whippets"}
                                    {:idx     2
                                     :ikey    {:name    "Jim Zelda"
                                               :dob     1
                                               :address "Brummie"}
                                     :name    "Jim Zelda"
                                     :dob     1
                                     :address "Brummie"}])