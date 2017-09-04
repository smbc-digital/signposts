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

    (testing "adds areas to people data"
      (is (= (people/with-areas {:people {
                                          {:name "1"} {:data [{:postcode "SK1 1AA"} {:postcode "SK2 1AA"}
                                                              {:postcode nil} {:postcode ""} {:postcode "SK1 2AA"}]}
                                          {:name "2"} {:data []}
                                          {:name "3"} {:data [{:postcode "SK3 3AA"}]}}})

             {:people {
                       {:name "1"} {:data  [{:postcode "SK1 1AA"} {:postcode "SK2 1AA"}
                                            {:postcode nil} {:postcode ""} {:postcode "SK1 2AA"}]
                                    :areas #{"SK1" "SK2"}}
                       {:name "2"} {:data  []
                                    :areas #{}}
                       {:name "3"} {:data  [{:postcode "SK3 3AA"}]
                                    :areas #{"SK3"}}}})))

    (testing "ranks people by whether locked, then score and sort order of surname"

      (is (= (people/with-rank {:people {{:name "A AB"} {:name "A AB" :score 1}
                                         {:name "Z AA"} {:name "Z AA" :score 1}
                                         {:name "Z AD"} {:name "Z AD" :score 1 :locked? true}
                                         {:name "A AC"} {:name "A AC" :score 2}}})

             {:people {{:name "Z AD"} {:name    "Z AD"
                                       :score   1
                                       :rank    1
                                       :locked? true}
                       {:name "A AC"} {:name  "A AC"
                                       :score 2
                                       :rank  2}
                       {:name "Z AA"} {:name  "Z AA"
                                       :score 1
                                       :rank  3}
                       {:name "A AB"} {:name  "A AB"
                                       :score 1
                                       :rank  4}}})))

    (testing "comes together with everyone displayed to start with"

      (with-redefs
        [people/group-keys [:name]
         c/colour-priority [:red :blue]]

        (testing "with people not highlighted already if too many"

          (let [result (people/from-data {:result [{:name "N1" :score 1}
                                                   {:name "N3" :score 2}
                                                   {:name "N2" :score 3}
                                                   {:name "N1" :score 4}]})]

            (is (contains? result :color-mgr))
            (is (= (dissoc result :color-mgr :result :timespan)
                   {:highlighting-allowed? true
                    :people                {{:name "N1"} {:data  [{:name "N1" :score 1}
                                                                  {:name "N1" :score 4}]
                                                          :score 4
                                                          :rank  1
                                                          :areas #{}}
                                            {:name "N2"} {:data  [{:name "N2" :score 3}]
                                                          :score 3
                                                          :rank  2
                                                          :areas #{}}
                                            {:name "N3"} {:data  [{:name "N3" :score 2}]
                                                          :score 2
                                                          :rank  3
                                                          :areas #{}}}})))))))

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
             [{:id 1} {:id 2} {:id 3} {:id 4}])))

    (testing "all highlighted events can be retrieved"
      (is (= (people/highlighted-events {:people {{:name "N1"} {:highlighted? true
                                                                :data         [{:id 1}
                                                                               {:id 2}]}
                                                  {:name "N2"} {:highlighted? false
                                                                :data         [{:id 3}]}
                                                  {:name "N3"} {:highlighted? true
                                                                :data         [{:id 4}]}}})
             [{:id 1} {:id 2} {:id 4}]))))

  (testing "highlights and colors"

    (testing "you can only highlight people when colours are available"

      (is (= (-> {:people    {{:name "A"} {}
                              {:name "B"} {}
                              {:name "C"} {}}
                  :color-mgr (s/new-colour-manager [:red :green])}
                 (people/toggle-highlight-person {:name "B"})
                 (people/toggle-highlight-person {:name "A"})
                 (dissoc :color-mgr))

             {:people                {{:name "A"} {:highlighted? true
                                                   :color        :green}
                                      {:name "B"} {:highlighted? true
                                                   :color        :red}
                                      {:name "C"} {}}
              :highlighting-allowed? false}))))

  (testing "printing a summary of results"
    (testing "multiple events from multiple people"
      (let [data {:people {{:name "N1"} {:data [{:name "N1" :score 1 :id 1}
                                                {:name "N1" :score 4 :id 2}]}
                           {:name "N2"} {:data [{:name "N2" :score 3 :id 3}]}
                           {:name "N3"} {:data [{:name "N3" :score 2 :id 4}]}}
                  :total  4}]
        (is (= (people/results-summary data)
               "Your search returned 4 events concerning 3 people"))))
    (testing "single event from 1 person"
      (let [data {:people {{:name "N1"} {:data [{:name "N1" :score 1 :id 1}]}}
                  :total  1}]
        (is (= (people/results-summary data)
               "Your search returned 1 event concerning 1 person")))))

  (testing "event selection"

    (with-redefs
      [people/group-keys [:name]]

      (testing "select event also marks the related person"
        (let [result (people/toggle-event {:people {{:name "N1"} {}
                                                    {:name "N2"} {}
                                                    {:name "N3"} {}}}
                                          {:name "N2" :event-id 1})]
          (is (= (:selected-event result) {:name "N2" :event-id 1}))
          (is (= (get-in result [:people {:name "N2"} :has-selected-event?]) true))))

      (testing "select event also unmarks any other person"
        (let [result (people/toggle-event {:people         {{:name "N1"} {:has-selected-event? true
                                                                          :data                [{:name "N1" :event-id 1}]}
                                                            {:name "N2"} {}
                                                            {:name "N3"} {}}
                                           :selected-event {:name "N1" :event-id 1}}
                                          {:name "N2" :event-id 2})]
          (is (= (:selected-event result) {:name "N2" :event-id 2}))
          (is (= (get-in result [:people {:name "N2"} :has-selected-event?]) true))
          (is (nil? (get-in result [:people {:name "N1"} :has-selected-event?])))))

      (testing "deselect event removes all selection indicators"
        (let [result (people/deselect-event {:people         {{:name "N1"} {}
                                                              {:name "N2"} {:has-selected-event? true}
                                                              {:name "N3"} {}}
                                             :selected-event {:name "N2"}})]
          (is (nil? (:selected-event result)))
          (is (nil? (get-in result [:people {:name "N2"} :has-selected-event?])))))))

  (testing "locking behaviour"

    (with-redefs
      [people/group-keys [:name]]

      (let [data {:people {{:name "N1"} {:data         [{:name "N1" :score 1 :id 1}
                                                        {:name "N1" :score 4 :id 2}]
                                         :locked?      true
                                         :highlighted? true}
                           {:name "N2"} {:data [{:name "N2" :score 3 :id 3}]}
                           {:name "N3"} {:data    [{:name "N3" :score 2 :id 4}]
                                         :locked? true}}

                  :result [{:name "N1" :score 5 :id 5}      ; existing addition to locked items
                           {:name "N2" :score 6 :id 6}
                           {:name "N4" :score 7 :id 7}
                           {:name "N1" :score 1 :id 1}]}]   ; existing duplicate of locked item

        (testing "we can lock a person"
          (is (= (-> {:people {{:name "N1"} {}}}
                     (people/lock {:name "N1"}))
                 {:people {{:name "N1"} {:locked? true}}})))

        (testing "we can unlock a person"
          (is (= (-> {:people {{:name "N1"} {}}}
                     (people/unlock {:name "N1"}))
                 {:people {{:name "N1"} {:locked? false}}})))

        (testing "we can extract the locked people"
          (is (= (people/locked-people data)
                 {{:name "N1"} {:data         [{:name "N1" :score 1 :id 1}
                                               {:name "N1" :score 4 :id 2}]
                                :highlighted? true
                                :locked?      true}
                  {:name "N3"} {:data    [{:name "N3" :score 2 :id 4}]
                                :locked? true}})))

        (testing "we can extract the locked events"
          (is (= (people/locked-events data)
                 [{:name "N1" :score 1 :id 1}
                  {:name "N1" :score 4 :id 2}
                  {:name "N3" :score 2 :id 4}])))

        (testing "when locked people already exist they are retained"

          (let [resulting-people (:people (people/from-data data))]

            (is (= (get resulting-people {:name "N1"})
                   {:data         [{:name "N1" :score 1 :id 1}
                                   {:name "N1" :score 4 :id 2}
                                   {:name "N1" :score 5 :id 5}]
                    :score        5
                    :areas        #{}
                    :rank         1
                    :locked?      true
                    :highlighted? true}))

            (is (= (select-keys (get resulting-people {:name "N2"}) [:data]) {:data [{:name "N2" :score 6 :id 6}]}))

            (is (= (select-keys (get resulting-people {:name "N3"}) [:data]) {:data [{:name "N3" :score 2 :id 4}]}))

            (is (= (select-keys (get resulting-people {:name "N4"}) [:data]) {:data [{:name "N4" :score 7 :id 7}]}))))

        (testing "colouring and locking"

          (with-redefs
            [c/colour-priority [:red :blue]]

            (testing "availability of colours is managed correctly"

              (let [!data (atom {:result [{:name :A :id 1}
                                          {:name :B :id 2}
                                          {:name :C :id 3}]})]

                ; retrieve initial result set
                (swap! !data people/from-data)

                (is (= 3 (count (:people @!data))))
                (is (true? ((:available? (:color-mgr @!data)))))

                ; highlight C then highlight A
                (swap! !data #(-> %
                                  (people/toggle-highlight-person {:name :C})
                                  (people/toggle-highlight-person {:name :A})))

                (is (= :blue (:color (get (:people @!data) {:name :A}))))
                (is (= :red (:color (get (:people @!data) {:name :C}))))
                (is (false? (:highlighting-allowed? @!data)))

                ; lock C & B
                (swap! !data #(-> %
                                  (people/lock {:name :C})
                                  (people/lock {:name :B})))

                ; modify the results from a new search
                (swap! !data assoc :result [{:name :D :id 4}
                                            {:name :C :id 5}])

                ; and handle the search coming back which also contains locked people
                (swap! !data people/from-data)

                ; people have changed; D arrives and A departs
                (is (= 3 (count (:people @!data))))
                (is (true? (contains? (:people @!data) {:name :D})))
                (is (false? (contains? (:people @!data) {:name :A})))

                ; C's events has been merged
                (is (= [{:name :C :id 3} {:name :C :id 5}] (:data (get (:people @!data) {:name :C}))))

                ; C remains highlighted & locked
                (is (= :red (:color (get (:people @!data) {:name :C}))))
                (is (true? (:locked? (get (:people @!data) {:name :C}))))

                ; colours remain available so highlighting is allowed
                (is (true? ((:available? (:color-mgr @!data)))))
                (is (true? (:highlighting-allowed? @!data)))

                ; highlight D
                (swap! !data #(-> %
                                  (people/toggle-highlight-person {:name :D})))

                ; now D takes the blue and no more highlighting is available
                (is (= :blue (:color (get (:people @!data) {:name :D}))))
                (is (false? (:highlighting-allowed? @!data))))))))))

  )