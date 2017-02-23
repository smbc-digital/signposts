(ns visualise.common.ui.flot-data-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.ui.flot-data :as fd]))

(def some-data [{:timestamp    1
                 :event-source :gmp
                 :event-type   :asbo
                 :name         "Jim"}
                {:timestamp    3
                 :event-source :gmp
                 :event-type   :asbo
                 :name         "Richard"}
                {:timestamp    2
                 :event-source :schools
                 :event-type   :exclusion
                 :name         "Jim"}])

(fact "should create y-axis labels"
      (fd/y-axis some-data) => {:min      0
                                :max      3
                                :position :right
                                :ticks    [[1 "exclusion"] [2 "asbo"]]})

(fact "should create a data series per person"
      (fd/series-data :name some-data) => [{:label "Jim"
                                            :data  [[1 2] [2 1]]}
                                           {:label "Richard"
                                            :data  [[3 2]]}])

; when two people have the same event type recorded on the same day they will be plotted directly
; on top of each other unless we adjust the data to create a spread on the y axis
(fact "should adjust the series data to disambiguate collisions of series on the same day"
      (let [result
            (fd/series-data :name [{:timestamp    1
                                    :event-source :gmp
                                    :event-type   :asbo
                                    :name         "Jim"}
                                   {:timestamp    1
                                    :event-source :gmp
                                    :event-type   :asbo
                                    :name         "Richard"}])]
        (first (:data (first result))) => (just [1 (roughly 0.975 0.001)])
        (first (:data (last result))) => (just [1 (roughly 1.025 0.001)])))

(fact "should produce a blur of numbers"
      (let [error 1/1000]
        (fd/blurred 2 1) => (just [(roughly 2.0 error)])

        (fd/blurred 2 3) => (just [(roughly 1.95 error)
                                   (roughly 2.0 error)
                                   (roughly 2.05 error)])

        (fd/blurred 2 4) => (just [(roughly 1.925 error)
                                   (roughly 1.975 error)
                                   (roughly 2.025 error)
                                   (roughly 2.075 error)])))




