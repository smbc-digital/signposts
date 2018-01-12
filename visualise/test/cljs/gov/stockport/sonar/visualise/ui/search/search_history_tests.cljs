(ns gov.stockport.sonar.visualise.ui.search.search-history-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state !search-history !data]]
            [gov.stockport.sonar.visualise.ui.search.search-history :as sh]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :refer [query-types]]
            ))

(deftest search-history-tests  "Test search history"
  (testing "testing to-guery-item"
    (is (= (sh/to-query-item {:query-type :address :search-term "SK2"}) {:field-name :address :field-type :address-with-postcode :query "SK2"}))
    (is (= (sh/to-query-item {:query-type :name :search-term "sm*"}) {:field-name :name :field-type :wildcard :query "sm*"})
    ))

  (testing  "testing query-from-search-control-state"
    (is (= (sh/query-from-search-control-state [{:query-type :address :search-term "SK2"}{:query-type :name :search-term "sm*"}])
           [{:field-name :address :field-type :address-with-postcode :query "SK2"} {:field-name :name :field-type :wildcard :query "sm*"}]))))



