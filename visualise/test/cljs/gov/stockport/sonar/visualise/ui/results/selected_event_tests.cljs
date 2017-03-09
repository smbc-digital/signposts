(ns gov.stockport.sonar.visualise.results.selected-event-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]))

(deftest display-selected-event-data

  (testing "should pull out common keys that exist, and in specified order"

    (is (= (se/selected-kvs {:postcode "PC" :dob "DOB" :name "NAME" :address "ADDRESS" :score 5})
           [[:name "NAME"]
            [:dob "DOB"]
            [:address "ADDRESS"]
            [:postcode "PC"]
            [:score 5]])))

  (testing "should leave missing standard keys blank"
    (is (= (se/selected-kvs {:address "ADDRESS" :dob "DOB"})
           [[:name ""]
            [:dob "DOB"]
            [:address "ADDRESS"]
            [:postcode ""]
            [:score ""]])))
  ;
  (testing "should add in other fields alphabetically"
    (is (= (se/selected-kvs {:zebra "ZZ" :aardvark "AA" :llama "LL" })
           [[:name ""]
            [:dob ""]
            [:address ""]
            [:postcode ""]
            [:score ""]
            [:aardvark "AA"]
            [:llama "LL"]
            [:zebra "ZZ"]])))

  (testing "should ignore timestamps"
    (is (= (se/selected-kvs {:zebra "ZZ" :aardvark "AA" :timestamp "TIMESTAMP"})
           [[:name ""]
            [:dob ""]
            [:address ""]
            [:postcode ""]
            [:score ""]
            [:aardvark "AA"]
            [:zebra "ZZ"]])))

  ; exclude timestamp initially

  )
