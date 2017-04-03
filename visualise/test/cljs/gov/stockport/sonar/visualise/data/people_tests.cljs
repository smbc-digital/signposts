(ns gov.stockport.sonar.visualise.data.people-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.data.colours :as c]))

(deftest people-tests

  (testing "building people from data returned in search"

    (testing "groups data by person"

      (with-redefs
        [people/group-keys [:name :dob]]

        (is (= (people/by-people [{:name "N1" :dob "D1" :other "data-1-1"}
                                  {:name "N3" :dob "D3" :other "data-3-1"}
                                  {:name "N2" :dob "D2" :other "data-2-1"}
                                  {:name "N1" :dob "D1" :other "data-1-2"}])

               {{:name "N1" :dob "D1"} {:data [{:name "N1" :dob "D1" :other "data-1-1"}
                                               {:name "N1" :dob "D1" :other "data-1-2"}]}
                {:name "N2" :dob "D2"} {:data [{:name "N2" :dob "D2" :other "data-2-1"}]}
                {:name "N3" :dob "D3"} {:data [{:name "N3" :dob "D3" :other "data-3-1"}]}}))))

    (testing "adds max score to people data"
      (is (= (people/with-max-score {{:name "1"} {:data [{:score 3} {:score 1} {:score nil} {:score 2}]}
                                     {:name "2"} {:data []}
                                     {:name "3"} {:data [{:score 4}]}})

             {{:name "1"} {:data  [{:score 3} {:score 1} {:score nil} {:score 2}]
                           :score 3}
              {:name "2"} {:data  []
                           :score nil}
              {:name "3"} {:data  [{:score 4}]
                           :score 4}})))

    (testing "ranks people by score and sort order of surname"

      (is (= (people/with-rank {{:name "A AB"} {:name "A AB" :score 1}
                                {:name "Z AA"} {:name "Z AA" :score 1}
                                {:name "A AC"} {:name "A AC" :score 2}})

             {{:name "Z AA"} {:name  "Z AA"
                              :score 1
                              :rank  2}
              {:name "A AB"} {:name  "A AB"
                              :score 1
                              :rank  3}
              {:name "A AC"} {:name  "A AC"
                              :score 2
                              :rank  1}})))

    (testing "display all people"
      (is (= (people/with-all-shown {{:name "A"} {}
                                     {:name "B"} {}})

             {{:name "A"} {:display true}
              {:name "B"} {:display true}})))

    (testing "adds colour-coding to people"

      (with-redefs
        [c/colour-priority [:red :blue]]

        (testing "when there are more people than colours"


          (is (= (people/with-colour-coding {{:name "A"} {:rank 1}
                                             {:name "B"} {:rank 2}
                                             {:name "C"} {:rank 3}})

                 {{:name "A"} {:rank 1 :color :black}
                  {:name "B"} {:rank 2 :color :black}
                  {:name "C"} {:rank 3 :color :black}})))

        (testing "when there are fewer people than colours"

          (is (= (people/with-colour-coding {{:name "A"} {:rank 1}
                                             {:name "B"} {:rank 2}})

                 {{:name "A"} {:rank 1 :color :red}
                  {:name "B"} {:rank 2 :color :blue}})))))

    (testing "builds people from data"
      (with-redefs
        [people/group-keys [:name]]

        (is (= (people/from-data [{:name "N1" :score 1}
                                  {:name "N3" :score 2}
                                  {:name "N2" :score 3}
                                  {:name "N1" :score 4}])

               {{:name "N1"} {:data    [{:name "N1" :score 1}
                                        {:name "N1" :score 4}]
                              :score   4
                              :rank    1
                              :color   :red
                              :display true}
                {:name "N2"} {:data    [{:name "N2" :score 3}]
                              :score   3
                              :rank    2
                              :color   :yellow
                              :display true}
                {:name "N3"} {:data    [{:name "N3" :score 2}]
                              :score   2
                              :rank    3
                              :color   :green
                              :display true}})))))

  (testing "retrieving people"

    (testing "by rank is possible"

      (is (= (people/by-rank {:people {{:name "A"} {:rank 4}
                                       {:name "B"} {:rank 1}
                                       {:name "C"} {:rank 6}}})

             [[{:name "B"} {:rank 1}]
              [{:name "A"} {:rank 4}]
              [{:name "C"} {:rank 6}]])))))
