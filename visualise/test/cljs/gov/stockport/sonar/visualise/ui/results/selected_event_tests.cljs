(ns gov.stockport.sonar.visualise.results.selected-event-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]))

(deftest display-selected-event-data

  (testing "provision of selected key-values"

    (testing "behaviour of standard keys"
      (with-redefs
        [se/standard-keys [:b :a]]

        (testing "should pull out common keys that exist, and in specified order"
          (is (= (se/selected-kvs {:a "A" :b "B"}) [[:b "B"] [:a "A"]])))

        (testing "should default values for common keys to blanks if they don't exist"
          (is (= (se/selected-kvs {:a "A"}) [[:b ""] [:a "A"]])))

        (testing "should exclude the id key"
          (is (= (se/selected-kvs {:a "A" :b "B" :id ""}) [[:b "B"] [:a "A"]])))

        (testing "should add values for other keys to the end of the list in alphabetical order of key"
          (is (= (se/selected-kvs {:llama "LL" :zebra "ZZ" :aardvark "AA"})
                 [[:b ""]
                  [:a ""]
                  [:aardvark "AA"]
                  [:llama "LL"]
                  [:zebra "ZZ"]])))))

    (testing "timestamp is parsed"
      (with-redefs
        [se/standard-keys [:timestamp]]

        (testing "should format timestamp nicely"
          (is (= (se/selected-kvs {:timestamp (t/date-time 2017 12 15 23 12 12)})
                 [[:timestamp "15 Dec 2017 23:12:12"]]))))))

  (testing "production of single row entry"
    (is (= (se/row [:aardvark 1]) [:tr [:th "Aardvark"] [:td 1]]))))
