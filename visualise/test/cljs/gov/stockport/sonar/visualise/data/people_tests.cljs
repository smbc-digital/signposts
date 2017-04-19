(ns gov.stockport.sonar.visualise.data.people-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.util.stack :as s]
            [gov.stockport.sonar.visualise.data.colours :as c]
            [gov.stockport.sonar.visualise.data.people :as people]))

(deftest people-tests

  (testing "building people from data"

    (testing "groups data by person"

      (with-redefs
        [people/group-keys [:name :dob]]

        (is (= (people/by-people {:result [{:name "N1" :dob "D1" :other "data-1-1"}
                                           {:name "N3" :dob "D3" :other "data-3-1"}
                                           {:name "N2" :dob "D2" :other "data-2-1"}
                                           {:name "N1" :dob "D1" :other "data-1-2"}]})

               {:result [{:name "N1" :dob "D1" :other "data-1-1"}
                         {:name "N3" :dob "D3" :other "data-3-1"}
                         {:name "N2" :dob "D2" :other "data-2-1"}
                         {:name "N1" :dob "D1" :other "data-1-2"}]

                :people {{:name "N1" :dob "D1"} {:data [{:name "N1" :dob "D1" :other "data-1-1"}
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

        (let [result (people/from-data {:result [{:name "N1" :score 1}
                                                 {:name "N3" :score 2}
                                                 {:name "N2" :score 3}
                                                 {:name "N1" :score 4}]})]

          (is (contains? result :color-stack))
          (is (= (dissoc result :color-stack :result)
                 {:show-only-highlighted? false
                  :all-collapsed?         false
                  :highlighting-allowed?  true
                  :people                 {{:name "N1"} {:data       [{:name "N1" :score 1}
                                                                      {:name "N1" :score 4}]
                                                         :score      4
                                                         :rank       1
                                                         :collapsed? false}
                                           {:name "N2"} {:data       [{:name "N2" :score 3}]
                                                         :score      3
                                                         :rank       2
                                                         :collapsed? false}
                                           {:name "N3"} {:data       [{:name "N3" :score 2}]
                                                         :score      2
                                                         :rank       3
                                                         :collapsed? false}}}))))))

  (testing "retrieving people"

    (testing "by rank is possible"

      (is (= (people/by-rank {:people {{:name "A"} {:rank 4}
                                       {:name "B"} {:rank 1}
                                       {:name "C"} {:rank 6}}})

             [[{:name "B"} {:rank 1}]
              [{:name "A"} {:rank 4}]
              [{:name "C"} {:rank 6}]])))

    (testing "all the data can be retrieved"
      (is (= (people/all-events {:people {{:name "N1"} {:data [{:id 1}
                                                               {:id 2}]}
                                          {:name "N2"} {:data [{:id 3}]}
                                          {:name "N3"} {:data [{:id 4}]}}})
             [{:id 1} {:id 2} {:id 3} {:id 4}]))))

  (testing "showing and hiding highlighted people"

    (is (= (-> (people/toggle-show-only-highlighted {:show-only-highlighted? false})
               (dissoc :color-stack))

           {:show-only-highlighted? true}))

    (is (= (-> (people/toggle-show-only-highlighted {:show-only-highlighted? true})
               (dissoc :color-stack))

           {:show-only-highlighted? false})))

  (testing "master-switch can be toggled to collapse everyone"

    (is (= (-> (people/toggle-collapse-all {:people         {{:name "A"} {:collapsed? true}
                                                             {:name "B"} {:collapsed? true}
                                                             {:name "C"} {:collapsed? false}}
                                            :all-collapsed? false})
               (dissoc :color-stack))

           {:people         {{:name "A"} {:collapsed? true}
                             {:name "B"} {:collapsed? true}
                             {:name "C"} {:collapsed? true}}
            :all-collapsed? true})))


  (testing "master-switch can be toggled to expand everyone"

    (is (= (-> (people/toggle-collapse-all {:people         {{:name "A"} {:collapsed? true}
                                                             {:name "B"} {:collapsed? true}
                                                             {:name "C"} {:collapsed? false}}
                                            :all-collapsed? true})
               (dissoc :color-stack))

           {:people         {{:name "A"} {:collapsed? false}
                             {:name "B"} {:collapsed? false}
                             {:name "C"} {:collapsed? false}}
            :all-collapsed? false})))

  (testing "highlights and colors"


    (testing "you can only highlight people when colours are available"

      (is (= (-> {:people      {{:name "A"} {}
                                {:name "B"} {}
                                {:name "C"} {}}
                  :color-stack (s/new-stack [:red :green])}
                 (people/toggle-highlight-person {:name "B"})
                 (people/toggle-highlight-person {:name "A"})
                 (dissoc :color-stack))

             {:people                {{:name "A"} {:highlighted? true
                                                   :color        :green}
                                      {:name "B"} {:highlighted? true
                                                   :color        :red}
                                      {:name "C"} {}}
              :highlighting-allowed? false
              })
          )

      )

    )

  ;(testing "has an effect on color"
  ;
  ;  (testing "individuals can be hidden"
  ;
  ;    (let [data (people/toggle-show-only-highlighted {:people {{:name "A"} {}
  ;                                                              {:name "B"} {}
  ;                                                              {:name "C"} {}}})]
  ;
  ;      (is (= (-> data
  ;                 (people/toggle-display-person {:name "A"})
  ;                 (dissoc :color-stack))
  ;
  ;             {:people                 {{:name "A"} {:highlighted? false :color :black}
  ;                                       {:name "B"} {:highlighted? true :color :yellow}
  ;                                       {:name "C"} {:highlighted? true :color :green}}
  ;              :show-only-highlighted? true}))))
  ;
  ;  (testing "hiding and showing can move color around"
  ;
  ;    (let [data (people/toggle-show-only-highlighted {:people {{:name "A"} {}
  ;                                                              {:name "B"} {}
  ;                                                              {:name "C"} {}}})]
  ;
  ;      (is (= (-> data
  ;                 (people/toggle-display-person {:name "A"})
  ;                 (people/toggle-display-person {:name "C"})
  ;                 (people/toggle-display-person {:name "A"})
  ;                 (dissoc :color-stack))
  ;
  ;             {:people                 {{:name "A"} {:highlighted? true :color :green}
  ;                                       {:name "B"} {:highlighted? true :color :yellow}
  ;                                       {:name "C"} {:highlighted? false :color :black}}
  ;              :show-only-highlighted? true}))))
  ;
  ;  (testing "colours can be added when there are more than 6 results"
  ;
  ;    (let [data (people/toggle-show-only-highlighted {:people                 {{:name "A"} {}
  ;                                                                              {:name "B"} {}
  ;                                                                              {:name "C"} {}
  ;                                                                              {:name "D"} {}
  ;                                                                              {:name "E"} {}
  ;                                                                              {:name "F"} {}
  ;                                                                              {:name "G"} {}}
  ;                                                     :show-only-highlighted? true})]
  ;
  ;      (is (= (-> data
  ;                 (people/toggle-display-person {:name "B"})
  ;                 (people/toggle-display-person {:name "D"})
  ;                 (people/toggle-display-person {:name "G"})
  ;                 (dissoc :color-stack))
  ;
  ;             {:people                 {{:name "A"} {:highlighted? false :color :black}
  ;                                       {:name "B"} {:highlighted? true :color :red}
  ;                                       {:name "C"} {:highlighted? false :color :black}
  ;                                       {:name "D"} {:highlighted? true :color :yellow}
  ;                                       {:name "E"} {:highlighted? false :color :black}
  ;                                       {:name "F"} {:highlighted? false :color :black}
  ;                                       {:name "G"} {:highlighted? true :color :green}}
  ;              :show-only-highlighted? false}))))
  ;
  ;  )
  )

(testing "locking behaviour"

  (let [data {:people {{:name "N1"} {:data    [{:name "N1" :score 1 :id 1}
                                               {:name "N1" :score 4 :id 2}]
                                     :locked? true}
                       {:name "N2"} {:data [{:name "N2" :score 3 :id 3}]}
                       {:name "N3"} {:data    [{:name "N3" :score 2 :id 4}]
                                     :locked? true}}

              :result [{:name "N1" :score 5 :id 5}          ; addition to locked items
                       {:name "N2" :score 6 :id 6}
                       {:name "N4" :score 7 :id 7}
                       {:name "N1" :score 1 :id 1}]}]       ; duplicate of locked item

    (testing "we can extract the locked events"

      (is (= (people/locked-events data)

             [{:name "N1" :score 1 :id 1}
              {:name "N1" :score 4 :id 2}
              {:name "N3" :score 2 :id 4}])))

    (testing "we can extract the set of locked keys"

      (is (= (people/locked-pkeys data)
             #{{:name "N1"}
               {:name "N3"}})))

    (testing "when locked people already exist they are retained"

      (let [result (:people (people/from-data data))]

        (is (= (select-keys (get result {:name "N1"}) [:data :locked?])
               {:data [{:name "N1" :score 1 :id 1}
                       {:name "N1" :score 4 :id 2}
                       {:name "N1" :score 5 :id 5}]}))

        (is (= (select-keys (get result {:name "N2"}) [:data]) {:data [{:name "N2" :score 6 :id 6}]}))

        (is (= (select-keys (get result {:name "N3"}) [:data]) {:data [{:name "N3" :score 2 :id 4}]}))

        (is (= (select-keys (get result {:name "N4"}) [:data]) {:data [{:name "N4" :score 7 :id 7}]}))))))
