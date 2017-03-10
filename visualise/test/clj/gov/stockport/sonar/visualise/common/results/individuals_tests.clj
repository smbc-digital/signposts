(ns gov.stockport.sonar.visualise.common.results.individuals-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.common.results.individuals :as i]))

(def some-data [{:name       "Jim Zelda"
                 :dob        1
                 :address    "Brummie"
                 :postcode   "B1 1TS"
                 :event-data "baltis"
                 :score      2.2}
                {:name       "Jim Zelda"
                 :dob        1
                 :address    "Brummie"
                 :postcode   "B1 1TS"
                 :event-data "weird accent"
                 :score      2.1}
                {:name       "Zeshan Aardvark"
                 :dob        2
                 :address    "Cockney"
                 :event-data "jellied eels"
                 :score      2.6}
                {:name       "Richard Sarky"
                 :dob        3
                 :address    "Whippets"
                 :event-data "flat caps"
                 :score      2.4}])

(fact "should group data into individuals by name, dob and address, ordered by descending score"
      (i/individuals some-data) => [{:idx     0
                                     :color   :red
                                     :ikey    {:name    "Zeshan Aardvark"
                                               :dob     2
                                               :address "Cockney"}
                                     :name    "Zeshan Aardvark"
                                     :dob     2
                                     :address "Cockney"
                                     :score   2.6}
                                    {:idx     1
                                     :color   :yellow
                                     :ikey    {:name    "Richard Sarky"
                                               :dob     3
                                               :address "Whippets"}
                                     :name    "Richard Sarky"
                                     :dob     3
                                     :address "Whippets"
                                     :score   2.4}
                                    {:idx      2
                                     :color    :green
                                     :ikey     {:name     "Jim Zelda"
                                                :dob      1
                                                :postcode "B1 1TS"
                                                :address  "Brummie"}
                                     :name     "Jim Zelda"
                                     :dob      1
                                     :postcode "B1 1TS"
                                     :address  "Brummie"
                                     :score    2.2}])