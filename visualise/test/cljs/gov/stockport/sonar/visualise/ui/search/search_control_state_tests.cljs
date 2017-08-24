(ns gov.stockport.sonar.visualise.ui.search.search-control-state-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]))

(deftest search-control-state-tests

  (testing "search term editor"

    (testing "starts with name as the selected field, and no search term or criteria"
      (scs/init! (fn [& _]))
      (is (= (scs/selected-control) :name))
      (is (= (scs/search-term) nil))
      (is (= (empty? (scs/search-criteria)) true)))

    (testing "you can change the selected field"
      (scs/init! (fn [& _]))
      (scs/set-selected-field! :address)
      (is (= (scs/selected-control) :address)))

    (testing "you can't add blank or empty search criteria"
      (scs/init! (fn [& _]))
      (scs/add-search-criteria!)
      (is (= (empty? (scs/search-criteria)) true))

      (scs/set-search-term! "")
      (scs/add-search-criteria!)
      (is (= (empty? (scs/search-criteria)) true)))

    (testing "adding a search criteria, clears the existing search term"
      (scs/init! (fn [& _]))
      (scs/set-selected-field! :address)
      (scs/set-search-term! "SK2")
      (scs/add-search-criteria!)

      (is (= "" (scs/search-term))))

    (testing "you can add and remove search criteria"
      (scs/init! (fn [& _]))
      (scs/set-selected-field! :address)
      (scs/set-search-term! "SK2")
      (scs/add-search-criteria!)

      (is (= (scs/search-criteria) [{:query-type :address :search-term "SK2"}]))

      (scs/set-selected-field! :name)
      (scs/set-search-term!"smith")
      (scs/add-search-criteria!)

      (is (= (scs/search-criteria) [{:query-type :address :search-term "SK2"}
                                           {:query-type :name :search-term "smith"}]))

      (scs/remove-search-criteria! :address)

      (is (= (scs/search-criteria) [{:query-type :name :search-term "smith"}])))

    (testing "you can add and remove search criteria without going via the search control fields"
      (scs/init!(fn [& _]))

      (scs/add-search-criteria! :address "SK2")
      (is (= (scs/search-criteria) [{:query-type :address :search-term "SK2"}]))

      (scs/add-search-criteria! :name "smith")
      (is (= (scs/search-criteria) [{:query-type :address :search-term "SK2"}
                                           {:query-type :name :search-term "smith"}]))

      (scs/remove-search-criteria! :address)
      (is (= (scs/search-criteria) [{:query-type :name :search-term "smith"}])))


    (testing "adding a new value for a given field, replaces the existing search term"
      (scs/init!(fn [& _]))
      (scs/add-search-criteria! :name "smith")
      (scs/add-search-criteria! :address "SK2")
      (scs/add-search-criteria! :address "SK2 latest")

      (is (= (scs/search-criteria) [{:query-type :name :search-term "smith"}
                                           {:query-type :address :search-term "SK2 latest"}])))

    (testing "callback is passed list of terms when there is a change"
      (let [!arguments-to-most-recent-callback (atom nil)]

        (scs/init!(fn [arg] (reset! !arguments-to-most-recent-callback arg)))

        (scs/add-search-criteria! :address "SK2")
        (scs/add-search-criteria! :name "smith")

        (is (= @!arguments-to-most-recent-callback (scs/search-criteria)))

        (scs/remove-search-criteria! :address)

        (is (= @!arguments-to-most-recent-callback (scs/search-criteria)))))))