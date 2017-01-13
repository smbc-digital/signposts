(ns visualise.v2
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [clojure.string :as str]))

(defn parse-timestamp [timestamp]
  (f/parse (:date-hour-minute-second-ms f/formatters) timestamp))

(defn date-parts [date]
  (str/split (f/unparse (f/formatter "dd:MMM:yyyy") date) #":"))

(def default-vb-w 365)
(def default-vb-h 150)

(defonce !controls (reagent/atom {:vb-x 0
                                  :vb-y 0
                                  :vb-w default-vb-w ; scale number of days being shown up to 2 years
                                  :vb-h default-vb-h ; vertical space for control
                                  :days 31
                                  }))

(defonce !current (reagent/atom {}))

(defn input-for [[key default]]
  (swap! !controls assoc key default)
  (fn []
    [:label key
     [:input {:placeholder (name key)
              :value       (key @!controls)
              :on-change   #(swap! !controls assoc key (-> % .-target .-value))}]]))


(defn inc-by [x] (fn [val] (max 0 (+ val x))))

(defn mult-by [x] (fn [val] (* val x)))

(defn controls []
  (fn []
    [:div
     [:input {:type     :button
              :value    "2 Yr"
              :on-click #(swap! !controls assoc :days 730)}]
     [:input {:type     :button
              :value    "1 Yr"
              :on-click #(swap! !controls assoc :days 365)}]
     [:input {:type     :button
              :value    "6 Months"
              :on-click #(swap! !controls assoc :days 182)}]
     [:input {:type     :button
              :value    "1 Month"
              :on-click #(swap! !controls assoc :days 31)}]
     [:input {:type     :button
              :value    "<<"
              :on-click #(swap! !controls update :vb-x (inc-by -100))}]
     [:input {:type     :button
              :value    "U"
              :on-click #(swap! !controls update :vb-y (inc-by -10))}]
     [:input {:type     :button
              :value    "=="
              :on-click #(swap! !controls assoc :vb-x 0 :vb-y 0 :vb-w default-vb-w)}]
     [:input {:type     :button
              :value    "D"
              :on-click #(swap! !controls update :vb-y (inc-by 10))}]
     [:input {:type     :button
              :value    ">>"
              :on-click #(swap! !controls update :vb-x (inc-by 100))}]
     [:input {:type     :button
              :value    "Z+"
              :on-click #(swap! !controls update :vb-w (mult-by 0.5))}]
     [:input {:type     :button
              :value    "Z-"
              :on-click #(swap! !controls update :vb-w (mult-by 2))}]
     [:span (:days @!controls) " " (:vb-w @!controls) " " (:vb-y @!controls)]
     ]))

(defn timeline [{:keys [y label]}]
  (fn []
    (let [{:keys [days vb-w]} @!controls
          x 0
          day-width (/ default-vb-w days)
          label-offset (+ x (:vb-x @!controls))
          length (* day-width days)]
      [:g
       [:text {:x label-offset :y (- y 10) :font-size "6px" :text-anchor :start :alignment-baseline :middle :stroke :black :stroke-width 0.25} label]
       [:g {:stroke :black :stroke-width 1}
        [:line {:x1 x :y1 y :x2 (+ x length) :y2 y}]
        (map
          (fn [day]
            (let [xt (+ x (/ day-width 2) (* day day-width))]
              ^{:key (gensym)}
              ;[:text {:stroke :grey :x xt :y y :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size 4} day]
              [:line {:stroke :grey
                      :x1 xt
                      :y1 (- y 3)
                      :x2 xt
                      :y2 (+ y 3)
                      :on-mouse-over #(reset! !current day)
                      :on-mouse-leave #(reset! !current nil)}]
              ))
          (range 0 days 3))]
       ])))

(defn ticks [{:keys [y]}]
  (fn []
    (let [{:keys [days vb-w]} @!controls
          x 0
          day-width (/ default-vb-w days)
          ticks (/ days 31)
          length (* day-width days)
          today (t/now)]
      [:g
       [:g {:stroke :grey :stroke-width 0.25}
        [:line {:x1 x :y1 y :x2 (+ x length) :y2 y}]
        (map
          (fn [day]
            (let [z (/ 1 (/ default-vb-w vb-w))
                  xtu (+ x (/ day-width 2) (* day day-width))
                  xt (/ xtu z)
                  yt (/ y z)
                  [dd mm yy] (date-parts (t/minus today (t/days (- days day 1))))
                  tx (str "scale(" z "," z ")")
                  ]
              ^{:key (gensym)} [:g
                                [:line {:stroke :grey :x1 xtu :y1 (+ y 20) :x2 xtu :y2 (+ y 300) :stroke-dasharray "1 1"}]
                                [:text {:x xt :y (+ yt 5) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px" :transform tx} dd]
                                [:text {:x xt :y (+ yt 10) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px" :transform tx} mm]
                                [:text {:x xt :y (+ yt 15) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px" :transform tx} yy]

                                ]))
          (range 0 days ticks))]
       ])))


(defn display []
  (fn []
    (let [{:keys [vb-x vb-y vb-w vb-h]} @!controls]
      [:div
       [:svg {:style {:border "solid 1px red"}
              :width                 "100%"
              :height                "100%"
              :view-box              (str/join " " (vals (select-keys @!controls [:vb-x :vb-y :vb-w :vb-h])))
              :preserve-aspect-ratio "xMinYMin meet"}
        [timeline {:y 50 :label "GMP - ASBO"}]
        [timeline {:y 80 :label "EIS - CIN"}]
        [timeline {:y 110 :label "SCHOOLS - AWOL"}]
        [ticks {:y 0 }]
        ]])))

(defn record []
  (fn []
    [:h1 @!current]))

(defn home-page []
  [:div
   [controls]
   [display]
   [record]])