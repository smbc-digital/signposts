(ns gov.stockport.sonar.visualise.ui.results.flot-axes-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.data.colours :refer [colour-map]]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]))

(def single-event {:timespan {:from-date     (t/date-time 2017)
                              :selected-from (t/date-time 2017)
                              :to-date       (t/date-time 2018)
                              :selected-to   (t/date-time 2018)}
                   :people   {:a {:data [{:event-type :asbo}]}}})

(def one-person {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                      {:timestamp 4 :event-type :caution}]
                                       :color        :red
                                       :highlighted? false}}})

(def two-people {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                      {:timestamp 4 :event-type :caution}]
                                       :rank         1
                                       :color        :red
                                       :highlighted? false}
                          {:name "B"} {:data         [{:timestamp 4 :event-type :zoology}
                                                      {:timestamp 1 :event-type :caution}]
                                       :rank         2
                                       :color        :blue
                                       :highlighted? false}}})

(def two-people-one-highlighted {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                                      {:timestamp 4 :event-type :caution}]
                                                       :rank         1
                                                       :highlighted? false
                                                       :color        :black}
                                          {:name "B"} {:data         [{:timestamp 4 :event-type :zoology}
                                                                      {:timestamp 1 :event-type :caution}]
                                                       :rank         2
                                                       :highlighted? true
                                                       :color        :red}}})

(def multiple-highlights {:people {{:name "A"} {:data [{:timestamp 1 :event-type :asbo}] :highlighted? true}
                                   {:name "B"} {:data [{:timestamp 2 :event-type :asbo}] :highlighted? false}
                                   {:name "C"} {:data [{:timestamp 3 :event-type :asbo}] :highlighted? true}
                                   {:name "D"} {:data [{:timestamp 4 :event-type :asbo}] :highlighted? false}
                                   {:name "E"} {:data [{:timestamp 5 :event-type :asbo}] :highlighted? false}
                                   {:name "F"} {:data [{:timestamp 6 :event-type :asbo}] :highlighted? true}}})


(def colliding-data {:people {{:name "A"} {:data         [{:timestamp 1 :event-type :asbo}
                                                          {:timestamp 1 :event-type :asbo}
                                                          {:timestamp 4 :event-type :caution}]
                                           :rank         1
                                           :color        :red
                                           :highlighted? false}
                              {:name "B"} {:data         [{:timestamp 1 :event-type :asbo}
                                                          {:timestamp 4 :event-type :caution}]
                                           :rank         2
                                           :color        :blue
                                           :highlighted? false}}})

(deftest flot-axes

  (testing "x-axis"

    (testing "defaults are set"
      (let [result (fa/x-axis single-event)]
        (is (= (:mode result) "time"))
        (is (= (:timeFormat result) "%Y/%m/%d"))
        (is (= (:minTickSize result) [1 "day"]))))

    (testing "timespan is used"
      (let [result (fa/x-axis single-event)]
        (is (t/= (:min result) (t/date-time 2017)))
        (is (t/= (:max result) (t/date-time 2018))))))

  (testing "y-axis"

    (testing "label map is based on event types"
      (is (= (fa/label-map {:people {:a {:data [{:event-type :asbo}]}
                                     :b {:data [{:event-type :zoology}]}
                                     :c {:data [{:event-type :caution}]}
                                     :d {:data [{:event-type :asbo}]}}})

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
      (let [result (fa/y-axis two-people)]
        (is (= (:max result) 4))
        (is (= (:ticks result) [[1 "zoology"] [2 "caution"] [3 "asbo"]])))))

  (testing "data points"

    (with-redefs
      [fa/collision-key (fn [event] (select-keys event [:timestamp :event-type]))]

      (testing "are derived as series based on people"

        (is (= (fa/data-points one-person)
               [{:points {:show true} :color (:red colour-map) :data [[1 2] [4 1]]}]))

        (is (= (fa/data-points two-people)
               [{:points {:show true} :color (:red colour-map) :data [[1 3] [4 2]]}
                {:points {:show true} :color (:blue colour-map) :data [[4 1] [1 2]]}])))

      (testing "may be turned off if showing only highlighted people"

        (is (= (fa/data-points (assoc two-people-one-highlighted :show-only-highlighted? true))
               [{:points {:show false} :color (:black colour-map) :data [[1 3] [4 2]]}
                {:points {:show true :fillColor false :fill 0.8} :color (:red colour-map) :data [[4 1] [1 2]]}])))

      (testing "will all be shown if showing all people"

        (is (= (fa/data-points (assoc two-people-one-highlighted :show-only-highlighted? false))
               [{:points {:show true} :color (:black colour-map) :data [[1 3] [4 2]]}
                {:points {:show true :fillColor false :fill 0.8} :color (:red colour-map) :data [[4 1] [1 2]]}])))

      (testing "events are shifted a little when they land on top of each other"
        (is (= (fa/data-points colliding-data)
               [{:points {:show true} :color (:red colour-map) :data [[1 1.9] [1 2] [4 0.95]]}
                {:points {:show true} :color (:blue colour-map) :data [[1 2.1] [4 1.05]]}])))

      (testing "highlighted data points are drawn last"
        (is (= (fa/data-points multiple-highlights)
               [{:points {:show true} :color (:black colour-map) :data [[2 1]]}
                {:points {:show true} :color (:black colour-map) :data [[4 1]]}
                {:points {:show true} :color (:black colour-map) :data [[5 1]]}
                {:points {:show true :fillColor false :fill 0.8} :color (:black colour-map) :data [[1 1]]}
                {:points {:show true :fillColor false :fill 0.8} :color (:black colour-map) :data [[3 1]]}
                {:points {:show true :fillColor false :fill 0.8} :color (:black colour-map) :data [[6 1]]}])))
      )

    (testing "collision keys provide appropriate equality"
      (is (= (fa/collision-key {:timestamp (t/date-time 2017) :event-type :asbo :id 1})
             (fa/collision-key {:timestamp (t/date-time 2017) :event-type :asbo :id 2}))))

    (testing "collisions are rounded to the nearest day"
      (is (= (fa/collision-key {:timestamp (t/date-time 2017 1 1 12) :event-type :asbo :id 1})
             (fa/collision-key {:timestamp (t/date-time 2017 1 1 13) :event-type :asbo :id 2}))))


    (testing "we can find the specific event based on series and data index"

      (is (= (fa/event-at two-people 0 0) {:timestamp 1 :event-type :asbo}))
      (is (= (fa/event-at two-people 1 1) {:timestamp 1 :event-type :caution}))
      (is (= (fa/event-at two-people 0 99) {}))
      (is (= (fa/event-at two-people 99 0) {}))
      (is (= (fa/event-at two-people 99 99) {})))))