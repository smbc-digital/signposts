(ns gov.stockport.sonar.visualise.ui.results.timeline-flot
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljsjs.flot.plugins.selection]
            [cljs-time.coerce :as tc]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]))

(defn options [m]
  (merge m {:grid   {:borderWidth     1
                     :minBorderMargin 20
                     :labelMagitrgin  10
                     :hoverable       true
                     :clickable       true
                     :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                     :margin          {:top    10
                                       :bottom 10
                                       :left   10}}
            :lines  {:show false}
            :points {:radius    8
                     :fillColor "rgba(255,255,255,0.8"}
            :legend {:show true}}))

(defn flot-render []
  [:div
   [:div.flot-selected {:style {:width "100%" :height 75}}]
   [:div.flot-timeline {:style {:width "100%" :height 500}}]])

(defn touch-data-to-force-rebind-click-handler [!data]
  (swap! !data update :plotinteraction #(not (or % false))))

(defn draw-selector [!data the-data options]
  (.plot (js/jQuery ".flot-selected")
         (clj->js the-data)
         (clj->js (-> options
                      (assoc-in [:yaxis :max] 0.001)
                      (assoc :selection {:mode    "x"
                                         :color   "#2e3292"
                                         :shape   "round"
                                         :minSize 10
                                         }))))

  (.bind (js/jQuery ".flot-selected") "plotselected"
         (fn [_ ranges]
           (touch-data-to-force-rebind-click-handler !data)
           (let [{{:keys [from to]} :xaxis} (js->clj ranges :keywordize-keys true)]
             (swap! !data (fn [data] (-> data
                                         (assoc-in [:timespan :selected-from] (tc/from-long from))
                                         (assoc-in [:timespan :selected-to] (tc/from-long to)))))))))

(defn draw-graph [!data the-data options]
  (let [flot (.plot js/jQuery (js/jQuery ".flot-timeline") (clj->js the-data) (clj->js options))]
    (if-let [{:keys [seriesIndex dataIndex]} (:point @!data)]
      (.highlight flot seriesIndex dataIndex))
    (.one (js/jQuery ".flot-timeline") "plotclick"
          (fn [_ _ item]
            (touch-data-to-force-rebind-click-handler !data)
            (if item
              (let [{:keys [datapoint dataIndex seriesIndex]} (js->clj item :keywordize-keys true)]
                (:selected-event (swap! !data assoc :selected-event (fa/event-at @!data seriesIndex dataIndex)))
                (swap! !data assoc :point {:datapoint datapoint :dataIndex dataIndex :seriesIndex seriesIndex}))
              (swap! !data dissoc :point :selected-event))))))

(defn draw-with [!data]
  (let []
    (draw-selector !data (fa/data-points @!data) (options {:xaxis (fa/selector-x-axis @!data)
                                                           :yaxis (fa/y-axis @!data)}))
    (draw-graph !data (fa/data-points @!data) (options {:xaxis (fa/x-axis @!data)
                                                        :yaxis (fa/y-axis @!data)}))))

(defn flot-component [!data]
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
         [flot-component !data @!data]
         [se/selected-event !data]]))))
