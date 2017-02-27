(ns visualise.ui.results.timeline-flot
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljs-time.core :as t]
            [cljs-time.periodic :as p]
            [visualise.common.ui.flot-data :as fd]))

(defn options [m]
  (clj->js
    (merge m {:grid   {:borderWidth     1
                       :minBorderMargin 20
                       :labelMargin     10
                       :hoverable       true
                       :clickable       true
                       :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                       :margin          {:top    10
                                         :bottom 10
                                         :left   10}}
              :xaxis  {:mode        "time"
                       :timeFormat  "%Y/%m/%d"
                       :minTickSize [1 "month"]
                       :min         (t/date-time 2015 1)
                       :max         (t/date-time 2016 12)}
              :lines  {:show false}
              :points {:show   true
                       :radius 5}
              :legend {:show true}})))

(defn flot-render []
  [:div.flot-timeline {:style {:width "100%" :height 500}}])

(defn draw-graph [the-data options]
  (.plot js/jQuery (js/jQuery ".flot-timeline") (clj->js the-data) options))

(defn draw-with [!data]
  (let [data (:result @!data)
        label-map (fd/y-axis-label-map (distinct (map :event-type data)))
        meta-data (fd/series-meta data)
        collisions (fd/collision-map data)
        fsd (fd/flot-series-data label-map collisions meta-data)]
    (draw-graph fsd (options {:yaxis (fd/y-axis (:result @!data))}))))


(defn flot-component [!data _]
  (fn []
    (reagent/create-class {:should-component-update (fn [& _] true)
                           :reagent-render          flot-render
                           :component-did-mount     (fn [] (draw-with !data))
                           :component-did-update    (fn [] (draw-with !data))})))

(defn timeline-flot [!data]
  (fn []
    (let [results (:result @!data)]
      (when (not-empty results)
        [:div.panel.panel-default.criteria-box
         [:div.panel-heading "Timeline Flot"]
         [:div.panel-body
          [flot-component !data (options {:yaxis (fd/y-axis results)})]
          ]]))))