(ns gov.stockport.sonar.visualise.ui.results.contacts-test
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.ui.results.contacts :as c]))

(def single-event {:timespan {:from-date (t/date-time 2017)
                              :to-date   (t/date-time 2018)}
                   :people   {:a {:data [{:event-type :asbo}]}}})

(def one-person {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                      {:timestamp 4 :event-type :caution}]
                                       :color        :red
                                       :highlighted? true}}})

(def two-people {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                      {:timestamp 4 :event-type :caution}]
                                       :rank         1
                                       :color        :red
                                       :highlighted? true}
                          {:name "B"} {:data         [{:timestamp 4 :event-type :zoology}
                                                      {:timestamp 1 :event-type :caution}]
                                       :rank         2
                                       :color        :blue
                                       :highlighted? true}}})

(def two-people-one-highlighted {:people {{:name "A"} {:data         [{:id 1 :timestamp 1 :event-type :asbo}
                                                                      {:id 3 :timestamp 4 :event-type :caution}]
                                                       :rank         1
                                                       :highlighted? false
                                                       :color        :black}
                                          {:name "B"} {:data         [{:id 2 :timestamp 4 :event-type :zoology}
                                                                      {:id 4 :timestamp 1 :event-type :caution}]
                                                       :rank         2
                                                       :highlighted? true
                                                       :color        :red}}})

(def two-people-both-highlighted {:people {{:name "A"} {:data         [{:id 1 :timestamp 1 :event-type :asbo}
                                                                       {:id 3 :timestamp 4 :event-type :caution}]
                                                        :rank         1
                                                        :highlighted? true
                                                        :color        :black}
                                           {:name "B"} {:data         [{:id 2 :timestamp 4 :event-type :zoology}
                                                                       {:id 4 :timestamp 1 :event-type :caution}]
                                                        :rank         2
                                                        :highlighted? true
                                                        :color        :red}}})

(def multiple-highlights {:people {{:name "A"} {:data [{:id :e1 :timestamp 1 :event-type :asbo}] :highlighted? true}
                                   {:name "B"} {:data [{:id :e2 :timestamp 2 :event-type :asbo}] :highlighted? false}
                                   {:name "C"} {:data [{:id :e3 :timestamp 3 :event-type :asbo}] :highlighted? true}
                                   {:name "D"} {:data [{:id :e4 :timestamp 4 :event-type :asbo}] :highlighted? false}
                                   {:name "E"} {:data [{:id :e5 :timestamp 5 :event-type :asbo}] :highlighted? false}
                                   {:name "F"} {:data [{:id :e6 :timestamp 6 :event-type :asbo}] :highlighted? true}}})


