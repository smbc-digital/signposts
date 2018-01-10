(ns gov.stockport.sonar.visualise.ui.search.search-history-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.search.search-history :as sh]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :refer [query-types]]
            ))

(deftest search-history-tests  "Test search history"
  (testing "testing to-guery-item"
    (is (= (sh/to-query-item {:query-type :address :search-term "SK2"}) {:query-type :address :field-type :address-with-postcode :query "SK2"}))
    (is (= (sh/to-query-item {:query-type :name :search-term "sm*"}) {:query-type :name :field-type :wildcard :query "sm*"})
    )))
