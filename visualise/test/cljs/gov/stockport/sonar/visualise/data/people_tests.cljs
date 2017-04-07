(ns gov.stockport.sonar.visualise.data.people-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.data.colours :as c]))

(deftest people-tests

  (testing "building people from data"

    (testing "groups data by person"

      (with-redefs
        [people/group-keys [:name :dob]]

        (is (= (people/by-people [{:name "N1" :dob "D1" :other "data-1-1"}
                                  {:name "N3" :dob "D3" :other "data-3-1"}
                                  {:name "N2" :dob "D2" :other "data-2-1"}
                                  {:name "N1" :dob "D1" :other "data-1-2"}])

               {:people {{:name "N1" :dob "D1"} {:data [{:name "N1" :dob "D1" :other "data-1-1"}
                                                        {:name "N1" :dob "D1" :other "data-1-2"}]}
                         {:name "N2" :dob "D2"} {:data [{:name "N2" :dob "D2" :other "data-2-1"}]}
                         {:name "N3" :dob "D3"} {:data [{:name "N3" :dob "D3" :other "data-3-1"}]}}}))))

    (testing "adds max score to people data"
      (is (= (people/with-max-score {:people {{:name "1"} {:data [{:score 3} {:score 1} {:score nil} {:score 2}]}
                                              {:name "2"} {:data []}
                                              {:name "3"} {:data [{:score 4}]}}})

             {:people {{:name "1"} {:data  [{:score 3} {:score 1} {:score nil} {:score 2}]
                                    :score 3}
                       {:name "2"} {:data  []
                                    :score nil}
                       {:name "3"} {:data  [{:score 4}]
                                    :score 4}}})))

    (testing "ranks people by score and sort order of surname"

      (is (= (people/with-rank {:people {{:name "A AB"} {:name "A AB" :score 1}
                                         {:name "Z AA"} {:name "Z AA" :score 1}
                                         {:name "A AC"} {:name "A AC" :score 2}}})

             {:people {{:name "Z AA"} {:name  "Z AA"
                                       :score 1
                                       :rank  2}
                       {:name "A AB"} {:name  "A AB"
                                       :score 1
                                       :rank  3}
                       {:name "A AC"} {:name  "A AC"
                                       :score 2
                                       :rank  1}}})))

    (testing "comes together with everyone displayed to start with"
      (with-redefs
        [people/group-keys [:name]]

        (is (= (people/from-data [{:name "N1" :score 1}
                                  {:name "N3" :score 2}
                                  {:name "N2" :score 3}
                                  {:name "N1" :score 4}])

               {:display-all? true
                :people       {{:name "N1"} {:data    [{:name "N1" :score 1}
                                                       {:name "N1" :score 4}]
                                             :score   4
                                             :rank    1
                                             :display true
                                             :color   :red}
                               {:name "N2"} {:data    [{:name "N2" :score 3}]
                                             :score   3
                                             :rank    2
                                             :display true
                                             :color   :yellow}
                               {:name "N3"} {:data    [{:name "N3" :score 2}]
                                             :score   2
                                             :rank    3
                                             :display true
                                             :color   :green}}})))))

  (testing "retrieving people"

    (testing "by rank is possible"

      (is (= (people/by-rank {:people {{:name "A"} {:rank 4}
                                       {:name "B"} {:rank 1}
                                       {:name "C"} {:rank 6}}})

             [[{:name "B"} {:rank 1}]
              [{:name "A"} {:rank 4}]
              [{:name "C"} {:rank 6}]]))))

  (testing "showing and hiding people"

    (testing "master-switch can be toggled to show everyone"

      (is (= (people/display-all {:people       {{:name "A"} {:display true}
                                                 {:name "B"} {:display true}
                                                 {:name "C"} {:display false}}
                                  :display-all? false})

             {:people       {{:name "A"} {:display true :color :red}
                             {:name "B"} {:display true :color :yellow}
                             {:name "C"} {:display true :color :green}}
              :display-all? true})))

    (testing "master-switch can be toggled to show no-one"

      (is (= (people/display-all {:people       {{:name "A"} {:display true}
                                                 {:name "B"} {:display true}
                                                 {:name "C"} {:display false}}
                                  :display-all? true})

             {:people       {{:name "A"} {:display false :color :black}
                             {:name "B"} {:display false :color :black}
                             {:name "C"} {:display false :color :black}}
              :display-all? false})))))