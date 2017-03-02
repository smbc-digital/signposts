(ns gov.stockport.sonar.visualise.common.ui.flot-data-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.common.ui.flot-data :as fd]))

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

(facts "about series meta data"

       (let [result (fd/series-meta some-data)]

         (fact "should include a set of individuals"

               (map :individual result) => [{:idx   0
                                             :color :red
                                             :ikey  {:name "Jim"}
                                             :name  "Jim"}
                                            {:idx   1
                                             :color :yellow
                                             :ikey  {:name "Richard"}
                                             :name  "Richard"}])

         (fact "series meta data should include all event payload"
               (let [event {:timestamp    1
                            :event-source :gmp
                            :event-type   :asbo
                            :name         "Jim"
                            :payload-1    "argh"
                            :payload-2    "urgh"}]
                 (fd/series-meta [event]) => [{:individual {:idx   0
                                                            :color :red
                                                            :ikey  {:name "Jim"}
                                                            :name  "Jim"}
                                               :data       [[1 :asbo (merge {:ikey {:name "Jim"}} event)]]}]))))

(fact "should create collisions map with no entries if there are no collisions"
      (fd/collision-map some-data) => {})

(fact "should create collisions map with collisions"
      (fd/collision-map [{:event-type :asbo :timestamp 1} {:event-type :asbo :timestamp 1}]) => {{:event-type :asbo :timestamp 1} 2})

(fact "should turn series meta data into flot series data"
      (fd/flot-series-data
        {:asbo 1 :exclusion 2}
        {}
        [{:individual {:idx   0
                       :color :red}
          :data       [[1 :asbo] [2 :exclusion]]}
         {:individual {:idx   1
                       :color :yellow}
          :data       [[3 :asbo]]}]) => [{:color "#f36624"
                                          :data  [[1 1] [2 2]]}
                                         {:color "#ffc502"
                                          :data  [[3 1]]}])

; when two people have the same event type recorded on the same day they will be plotted directly
; on top of each other unless we adjust the data to create a spread on the y axis
(fact "should adjust the series data to disambiguate collisions of series on the same day"
      (let [result (fd/flot-series-data
                     {:asbo 1 :exclusion 2}
                     {{:event-type :asbo :timestamp 1} 2}
                     [{:individual {:idx 0}
                       :data       [[1 :asbo]]}
                      {:individual {:idx 1}
                       :data       [[1 :asbo]]}])]
        (first (:data (first result))) => (just [1 (roughly 0.975 0.001)])
        (first (:data (last result))) => (just [1 (roughly 1.025 0.001)])))



