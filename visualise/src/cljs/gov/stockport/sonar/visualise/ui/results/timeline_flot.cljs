(ns gov.stockport.sonar.visualise.ui.results.timeline-flot
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljs-time.core :as t]
            [gov.stockport.sonar.visualise.common.ui.flot-data :as fd]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]))

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
                       :max         (t/date-time 2016 12)
                       }
              :lines  {:show false}
              :points {:show   true
                       :radius 5}
              :legend {:show true}})))

(defn flot-render []
  [:div.flot-timeline {:style {:width "100%" :height 500}}])

(defonce !item (atom {}))
(defonce !metad (atom {}))

(defn draw-graph [!data the-data meta-data options]
  (reset! !metad meta-data)
  (let [flot (.plot js/jQuery (js/jQuery ".flot-timeline") (clj->js the-data) options)]
    (if-let [{:keys [seriesIndex dataIndex]} (:point @!data)]
      (.highlight flot seriesIndex dataIndex))
    (.one (js/jQuery ".flot-timeline") "plotclick"
          (fn [_ _ item]
            (if item
              (let [{:keys [datapoint dataIndex seriesIndex]} (js->clj item :keywordize-keys true)
                    event-data (last (nth (:data (nth meta-data seriesIndex)) dataIndex))]
                (:selected-event (swap! !data assoc :selected-event event-data))
                (swap! !data assoc :point {:datapoint datapoint :dataIndex dataIndex :seriesIndex seriesIndex})))))))

(defn draw-with [!data]
  (let [data (:result @!data)
        label-map (fd/y-axis-label-map (distinct (map :event-type data)))
        meta-data (fd/series-meta data)
        collisions (fd/collision-map data)
        fsd (fd/flot-series-data label-map collisions meta-data)]
    (draw-graph !data fsd meta-data (options {:yaxis (fd/y-axis (:result @!data))}))))


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
        [:div
         [flot-component !data (options {:yaxis (fd/y-axis results)})]
         [se/selected-event !data]]))))
