(ns visualise.v2
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [visualise.data :refer [static-data]]
            [clojure.string :as str]))

(defn parse-timestamp [timestamp]
  (f/parse (:date-hour-minute-second-ms f/formatters) timestamp))

(defn simple [date]
  (f/unparse (f/formatter "dd:MMM:yyyy") date))

(def default-vb-w 365)
(def default-vb-h 150)

(defonce !controls (reagent/atom {:vb-x 0
                                  :vb-y 0
                                  :vb-w default-vb-w ; scale number of days being shown up to 2 years
                                  :vb-h default-vb-h ; vertical space for control
                                  :days 31
                                  }))

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
              :value    "++"
              :on-click #(swap! !controls update :vb-y (inc-by -10))}]
     [:input {:type     :button
              :value    "=="
              :on-click #(swap! !controls assoc :vb-x 0 :vb-y 0 :vb-w default-vb-w)}]
     [:input {:type     :button
              :value    "--"
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
     ]))

(defn timeline [{:keys [y label]}]
  (fn []
    (let [{:keys [days vb-w]} @!controls
          x 0
          day-width (/ vb-w days)
          label-offset (+ x (:vb-x @!controls))
          length (* day-width days)]
      [:g
       [:text {:x label-offset :y (- y 10) :font-size "6px" :text-anchor :start :alignment-baseline :middle :stroke :black :stroke-width 0.25} label]
       [:g {:stroke :black :stroke-width 1}
        [:line {:x1 x :y1 y :x2 (+ x length) :y2 y}]
        (map
          (fn [day]
            (let [xt (+ x (/ day-width 2) (* day day-width))]
              ^{:key (gensym)} [:line {:stroke :grey :x1 xt :y1 (- y 3) :x2 xt :y2 (+ y 3)}]))
          (range 0 days 2))]
       ])))



(defn ticks [{:keys [y]}]
  (fn []
    (let [{:keys [days vb-w]} @!controls
          x 0
          day-width (/ vb-w days)
          ticks (/ days 31)
          length (* day-width days)
          today (t/now)]
      [:g
       [:g {:stroke :grey :stroke-width 0.25}
        [:line {:x1 x :y1 y :x2 (+ x length) :y2 y}]
        (map
          (fn [day]
            (let [xt (+ x (/ day-width 2) (* day day-width))
                  label (simple (t/minus today (t/days (- days day 1))))
                  [dd mm yy] (str/split label #":")
                  ]
              ^{:key (gensym)} [:g
                                [:line {:stroke :grey :x1 xt :y1 (- y 300) :x2 xt :y2 (+ y 3)}]
                                [:text {:x xt :y (+ y 5) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px"} dd]
                                [:text {:x xt :y (+ y 10) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px"} mm]
                                [:text {:x xt :y (+ y 15) :text-anchor :middle :alignment-baseline :hanging :stroke-width 0.2 :font-size "4px"} yy]

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
        [timeline {:y 20 :label "GMP - ASBO"}]
        [timeline {:y 50 :label "EIS - CIN"}]
        [timeline {:y 80 :label "SCHOOLS - AWOL"}]
        [ticks {:y 110 }]

        ]])))


(defn home-page []
  [:div
   [controls]
   [display]])