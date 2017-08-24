(ns gov.stockport.sonar.visualise.ui.search.search-control-state-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]))

(def !state (atom {}))

(deftest search-control-state-tests

  (testing "search term editor"

    (testing "starts with name as the selected field, and no search term or criteria"
      (scs/init! !state (fn [& _]))
      (is (= (scs/selected-control !state) :name))
      (is (= (scs/search-term !state) nil))
      (is (= (empty? (scs/search-criteria !state)) true)))

    (testing "you can change the selected field"
      (scs/init! !state (fn [& _]))
      (scs/set-selected-field! !state :address)
      (is (= (scs/selected-control !state) :address)))

    (testing "you can't add blank or empty search criteria"
      (scs/init! !state (fn [& _]))
      (scs/add-search-criteria! !state)
      (is (= (empty? (scs/search-criteria !state)) true))

      (scs/set-search-term! !state "")
      (scs/add-search-criteria! !state)
      (is (= (empty? (scs/search-criteria !state)) true)))

    (testing "adding a search criteria, clears the existing search term"
      (scs/init! !state (fn [& _]))
      (scs/set-selected-field! !state :address)
      (scs/set-search-term! !state "SK2")
      (scs/add-search-criteria! !state)

      (is (= "" (scs/search-term !state))))

    (testing "you can add and remove search criteria"
      (scs/init! !state (fn [& _]))
      (scs/set-selected-field! !state :address)
      (scs/set-search-term! !state "SK2")
      (scs/add-search-criteria! !state)

      (is (= (scs/search-criteria !state) [{:query-type :address :search-term "SK2"}]))

      (scs/set-selected-field! !state :name)
      (scs/set-search-term! !state "smith")
      (scs/add-search-criteria! !state)

      (is (= (scs/search-criteria !state) [{:query-type :address :search-term "SK2"}
                                           {:query-type :name :search-term "smith"}]))

      (scs/remove-search-criteria! !state :address)

      (is (= (scs/search-criteria !state) [{:query-type :name :search-term "smith"}])))

    (testing "you can add and remove search criteria without going via the search control fields"
      (scs/init! !state (fn [& _]))

      (scs/add-search-criteria! !state :address "SK2")
      (is (= (scs/search-criteria !state) [{:query-type :address :search-term "SK2"}]))

      (scs/add-search-criteria! !state :name "smith")
      (is (= (scs/search-criteria !state) [{:query-type :address :search-term "SK2"}
                                           {:query-type :name :search-term "smith"}]))

      (scs/remove-search-criteria! !state :address)
      (is (= (scs/search-criteria !state) [{:query-type :name :search-term "smith"}])))


    (testing "adding a new value for a given field, replaces the existing search term"
      (scs/init! !state (fn [& _]))
      (scs/add-search-criteria! !state :name "smith")
      (scs/add-search-criteria! !state :address "SK2")
      (scs/add-search-criteria! !state :address "SK2 latest")

      (is (= (scs/search-criteria !state) [{:query-type :name :search-term "smith"}
                                           {:query-type :address :search-term "SK2 latest"}])))

    (testing "callback is passed list of terms when there is a change"
      (let [!arguments-to-most-recent-callback (atom nil)]

        (scs/init! !state (fn [arg] (reset! !arguments-to-most-recent-callback arg)))

        (scs/add-search-criteria! !state :address "SK2")
        (scs/add-search-criteria! !state :name "smith")

        (is (= @!arguments-to-most-recent-callback (scs/search-criteria !state)))

        (scs/remove-search-criteria! !state :address)

        (is (= @!arguments-to-most-recent-callback (scs/search-criteria !state)))))))