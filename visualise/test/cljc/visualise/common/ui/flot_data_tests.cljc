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

(fact "should provide numeric values for event types"
      (fd/y-axis-label-map [:asbo :exclusion :caution]) => {:exclusion 1
                                                            :caution   2
                                                            :asbo      3})

(fact "should create y-axis labels"
      (fd/y-axis some-data) => {:min      0
                                :max      3
                                :position :right
                                :ticks    [[1 "exclusion"] [2 "asbo"]]})

(fact "should create series-meta-data"
      (fd/series-meta some-data) => [{:individual {:idx  0
                                                   :ikey {:name "Jim"}
                                                   :name "Jim"}
                                      :data       [[1 :asbo] [2 :exclusion]]}
                                     {:individual {:idx  1
                                                   :ikey {:name "Richard"}
                                                   :name "Richard"}
                                      :data       [[3 :asbo]]}])

;(fact "should turn series meta data into flot series data"
;      (fd/flot-series-data
;        {:asbo 1 :exclusion 2}
;        [{:individual {:idx   0}
;          :data       [[1 :asbo] [2 :exclusion]]}
;         {:individual {:idx   1}
;          :data       [[3 :asbo]]}]) => [{:color :red
;                                          :data  [[1 1] [2 2]]}
;                                         {:color :blue
;                                          :data  [[3 1]]}])


;(fact "should create a data series per person"
;      (fd/series-data :name some-data) => [{:data [[1 2] [2 1]]}
;                                           {:data [[3 2]]}])
;
;; when two people have the same event type recorded on the same day they will be plotted directly
;; on top of each other unless we adjust the data to create a spread on the y axis
;(fact "should adjust the series data to disambiguate collisions of series on the same day"
;      (let [result
;            (fd/series-data :name [{:timestamp    1
;                                    :event-source :gmp
;                                    :event-type   :asbo
;                                    :name         "Jim"}
;                                   {:timestamp    1
;                                    :event-source :gmp
;                                    :event-type   :asbo
;                                    :name         "Richard"}])]
;        (first (:data (first result))) => (just [1 (roughly 0.975 0.001)])
;        (first (:data (last result))) => (just [1 (roughly 1.025 0.001)])))
;



