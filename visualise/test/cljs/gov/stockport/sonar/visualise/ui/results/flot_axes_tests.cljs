(ns gov.stockport.sonar.visualise.ui.results.flot-axes-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]))

(def single-event {:result   [{:timestamp (t/date-time 2016 12 1) :event-type :asbo}]
                   :timespan {:from-date     (t/date-time 2017)
                              :selected-from (t/date-time 2017)
                              :to-date       (t/date-time 2018)
                              :selected-to   (t/date-time 2018)}})

(def multiple-events {:result [{:timestamp (t/date-time 2016 11 1) :event-type :asbo}
                               {:timestamp (t/date-time 2016 12 1) :event-type :caution}]})

(def one-person {:result [{:timestamp 1 :event-type :asbo}
                          {:timestamp 4 :event-type :caution}
                          {:timestamp 2 :event-type :zoology}]
                 :people {{:name "A"} {:data    [{:timestamp 1 :event-type :asbo}
                                                 {:timestamp 4 :event-type :caution}]
                                       :color   :red
                                       :display true}}})

(def two-people {:result [{:timestamp 1 :event-type :asbo}
                          {:timestamp 1 :event-type :caution}
                          {:timestamp 4 :event-type :caution}
                          {:timestamp 4 :event-type :zoology}]
                 :people {{:name "A"} {:data    [{:timestamp 1 :event-type :asbo}
                                                 {:timestamp 4 :event-type :caution}]
                                       :rank    1
                                       :color   :red
                                       :display true}
                          {:name "B"} {:data    [{:timestamp 4 :event-type :zoology}
                                                 {:timestamp 1 :event-type :caution}]
                                       :rank    2
                                       :color   :blue
                                       :display true}}})

(def two-people-one-hidden {:result [{:timestamp 1 :event-type :asbo}
                                     {:timestamp 1 :event-type :caution}
                                     {:timestamp 4 :event-type :caution}
                                     {:timestamp 4 :event-type :zoology}]
                            :people {{:name "A"} {:data    [{:timestamp 1 :event-type :asbo}
                                                            {:timestamp 4 :event-type :caution}]
                                                  :rank    1
                                                  :display false
                                                  :color   :red}
                                     {:name "B"} {:data    [{:timestamp 4 :event-type :zoology}
                                                            {:timestamp 1 :event-type :caution}]
                                                  :rank    2
                                                  :display true
                                                  :color   :blue}}})

(def colliding-data {:result [{:timestamp 1 :event-type :asbo}
                              {:timestamp 1 :event-type :asbo}
                              {:timestamp 1 :event-type :asbo}
                              {:timestamp 4 :event-type :caution}
                              {:timestamp 4 :event-type :caution}]
                     :people {{:name "A"} {:data    [{:timestamp 1 :event-type :asbo}
                                                     {:timestamp 1 :event-type :asbo}
                                                     {:timestamp 4 :event-type :caution}]
                                           :rank    1
                                           :color   :red
                                           :display true}
                              {:name "B"} {:data    [{:timestamp 1 :event-type :asbo}
                                                     {:timestamp 4 :event-type :caution}]
                                           :rank    2
                                           :color   :blue
                                           :display true}}})

(deftest flot-axes

  (testing "x-axis"

    (testing "defaults are set"
      (let [result (fa/x-axis single-event)]
        (is (= (:mode result) "time"))
        (is (= (:timeFormat result) "%Y/%m/%d"))
        (is (= (:minTickSize result) [1 "month"]))))

    (testing "timespan is used"
      (let [result (fa/x-axis single-event)]
        (is (t/= (:min result) (t/date-time 2017)))
        (is (t/= (:max result) (t/date-time 2018))))))

  (testing "y-axis"

    (testing "label map is based on event types"
      (is (= (fa/label-map {:result [{:event-type :asbo}
                                     {:event-type :zoology}
                                     {:event-type :caution}
                                     {:event-type :asbo}]})

             {:zoology 1 :caution 2 :asbo 3})))

    (testing "defaults are set"
      (let [result (fa/y-axis single-event)]
        (is (= (:min result) 0))
        (is (= (:position result) :right))))

    (testing "ticks are good for single event type"
      (let [result (fa/y-axis single-event)]
        (is (= (:max result) 2))
        (is (= (:ticks result) [[1 "asbo"]]))))

    (testing "ticks are good for multiple event types"
      (let [result (fa/y-axis multiple-events)]
        (is (= (:max result) 3))
        (is (= (:ticks result) [[1 "caution"] [2 "asbo"]])))))

  (testing "data points"

    (testing "are derived as series based on people"

      (is (= (fa/data-points one-person)
             [{:points {:show true} :color (:red colour-map) :data [[1 3] [4 2]]}]))

      (is (= (fa/data-points two-people)
             [{:points {:show true} :color (:red colour-map) :data [[1 3] [4 2]]}
              {:points {:show true} :color (:blue colour-map) :data [[4 1] [1 2]]}])))

    (testing "may be turned off if the person is not displayed"
      (is (= (fa/data-points two-people-one-hidden)
             [{:points {:show false} :color (:red colour-map) :data [[1 3] [4 2]]}
              {:points {:show true} :color (:blue colour-map) :data [[4 1] [1 2]]}])))

    (testing "events are shifted a little when they land on top of each other"
      (is (= (fa/data-points colliding-data)
             [{:points {:show true} :color (:red colour-map) :data [[1 1.9] [1 2] [4 0.95]]}
              {:points {:show true} :color (:blue colour-map) :data [[1 2.1] [4 1.05]]}])))

    (testing "we can find the specific event based on series and data index"

      (is (= (fa/event-at two-people 0 0) {:timestamp 1 :event-type :asbo}))
      (is (= (fa/event-at two-people 1 1) {:timestamp 1 :event-type :caution}))
      (is (= (fa/event-at two-people 0 99) {}))
      (is (= (fa/event-at two-people 99 0) {}))
      (is (= (fa/event-at two-people 99 99) {})))))