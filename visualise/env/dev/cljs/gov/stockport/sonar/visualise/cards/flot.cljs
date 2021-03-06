(ns gov.stockport.sonar.visualise.cards.flot
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljs-time.core :as t]
            [cljs-time.periodic :as p])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-doc
  "
  ##Flot
  ")

(defn options []
  (clj->js {:grid   {:borderWidth     1
                     :minBorderMargin 20
                     :labelMargin     10
                     :hoverable       true
                     :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                     :margin          {:top    10
                                       :bottom 10
                                       :left   10}}
            :xaxis  {:min      0
                     :max      15
                     :tickSize 1}
            :yaxis  {:min      0
                     :max      4
                     :ticks    [[1 "GMP"] [2 "POLICE"] [3 "SCHOOLS"]]
                     :position :right
                     }
            :lines  {:show false}
            :points {:show true}
            :legend {:show true}}))

(defn simple-data []
  (clj->js [{:label "argh"
             :data  [[0 20] [1 1] [2 2] [3 3] [80 20]]}
            {:label "urgh"
             :data  [[5 20] [34 46.5]]}]))

(defn named-lines []
  (clj->js [{:label "Jim"
             :data  [[3 1] [5 1] [6 2] [4 1.95]]}           ; x is the date ; y is the source and the series is a person
            {:label "Pat"
             :data  [[5 2] [7 2] [4 1] [4 2.05] [7 3]]}
            {:data [[7 3] [9 3] [12 3]]}
            ]))

(defn flot-render []
  [:div#flot1 {:style {:width 700 :height 300}}])

(defn flot-did-mount [data-fn]
  (.plot js/jQuery (js/jQuery "#flot1") (clj->js (data-fn)) (options)))

(defn flot-component [data-fn]
  (reagent/create-class {:reagent-render      flot-render
                         :component-did-mount (fn [] (flot-did-mount data-fn))}))

(defcard-rg flot-simple-example [flot-component named-lines])

(defn ts-options []
  (clj->js {:grid   {:borderWidth     1
                     :minBorderMargin 20
                     :labelMargin     10
                     :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                     :margin          {:top    10
                                       :bottom 10
                                       :left   10}
                     :hoverable       true
                     :clickable       true}
            :xaxis  {:mode        "time"
                     :timeFormat  "%Y/%m/%d"
                     :minTickSize [1 "month"]
                     :min         (t/date-time 2013 1)
                     :max         (t/date-time 2013 12)}
            :yaxis  {:mode :categories
                     ;:min 0
                     ;:max 100
                     }
            :lines  {:show true}
            :points {:show   true
                     :radius 5
                     :fill   true
                     }
            :legend {:show true}}))


(defn random-event-data [source]
  (let [days (take 365 (p/periodic-seq (t/date-time 2010) (t/days 6)))]
    (partition 2 2 nil (interleave days source))))


(defonce random-time-series-data
         (let [days (take 365 (p/periodic-seq (t/date-time 2010) (t/days 6)))]
           (partition 2 2 nil (interleave days (repeatedly #(+ 10 (rand-int 80)))))))

(defn ts-data []
  (clj->js [{:label "some-time-series"
             :data  random-time-series-data}
            ]))

(defn event-data []
  (clj->js
    (map
      (fn [source]
        {:label source
         :data  (random-event-data (str source))})
      [:gmp :homes :schools])))

(defonce !tooltip-state (atom {:content "HELLO"}))

(defn tooltip []
  (fn []
    [:div#tooltip {:style {:position         :absolute
                           ;:display          :none
                           :border           "1px solid #fdd"
                           :top              (:top @!tooltip-state)
                           :left             (:left @!tooltip-state)
                           :padding          2
                           :background-color "#fee"
                           :opacity          0.8}}
     (:content @!tooltip-state)]))

(defn flot-ts-render []
  [:div#flot2 {:style {:width 700 :height 300}}])

(defn flot-ts-did-mount []
  (.plot js/jQuery (js/jQuery "#flot2") (clj->js (event-data)) (ts-options))
  (.bind (js/jQuery "#flot2") "plothover"
         (fn [event pos item]
           (if item
             (let [argh (js->clj item :keywordize-keys true)]
               (reset! !tooltip-state {:content "hello"
                                       :top     (+ (:pageX argh) 5)
                                       :left    (+ (:pageY argh) 5)
                                       })
               (reset! !tooltip-state nil))))))

(defn flot-time-series-component []
  (fn []
    (reagent/create-class {:reagent-render      flot-ts-render
                           :component-did-mount flot-ts-did-mount})))

(defcard-rg flot-with-time-series
            [flot-time-series-component])


;(defcard-rg flot-timeline []
;            )