(ns gov.stockport.sonar.visualise.ui.results.timeline-flot
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljsjs.flot.plugins.selection]
            [cljsjs.flot.plugins.resize]
            [cljs-time.coerce :as tc]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]
            [cljs-time.core :as t]
            [cljs-time.format :as f]
            [reagent.core :as r]))

(defn options [m]
  (merge m {:grid   {:borderWidth     1
                     :minBorderMargin 20
                     :labelMargin     10
                     :hoverable       true
                     :clickable       true
                     :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                     :margin          {:top    10
                                       :bottom 10
                                       :left   10}}
            :lines  {:show false}
            :points {:radius    8
                     :fillColor "rgba(255,255,255,0.8"}
            :legend {:show false}}))

(defn fmt [timestamp]
  (f/unparse (f/formatter "d MMMM YYYY") timestamp))

(defn graph-placeholder-with-description [!timespan]
  [:div
   [:div.flot-selected {:style {:width "100%" :height 100}}]
   (let [{:keys [:selected-from :selected-to]} @!timespan]
     [:div
      [:center
       [:span "graph below shows "]
       [:strong (fmt selected-from)]
       [:span " - "]
       [:strong (fmt selected-to)]]])
   [:div.flot-timeline {:style {:width "100%" :height 500}}]])

(defn touch-data-to-force-rebind-click-handler [!data]
  (swap! !data update :plotinteraction #(not (or % false))))

(defn draw-selector [!data the-data options]
  (let [!plotselected (atom nil)]
    (.plot (js/jQuery ".flot-selected")
           (clj->js the-data)
           (clj->js (-> options
                        (assoc :selection {:mode    "x"
                                           :shape   "round"
                                           :minSize 1
                                           }))))

    (.bind (js/jQuery ".flot-selected") "plotselected"
           (fn [_ ranges]
             (let [{{:keys [from to]} :xaxis} (js->clj ranges :keywordize-keys true)]
               (reset! !plotselected true)
               (swap! !data (fn [data] (-> data
                                           (assoc-in [:timespan :selected-from] (tc/from-long from))
                                           (assoc-in [:timespan :selected-to] (tc/from-long to))))))))

    (.bind (js/jQuery ".flot-selected") "plotclick"
           (fn [& _]
             (if (not @!plotselected)
               (do
                 (reset! !plotselected false)
                 (:timespan
                   (swap! !data (fn [{:keys [:timespan] :as data}]
                                  (-> data
                                      (assoc-in [:timespan :selected-from] (:from-date timespan))
                                      (assoc-in [:timespan :selected-to] (:to-date timespan)))))))
               (reset! !plotselected false))))))

(defn draw-graph [!data the-data options]
  (let [flot (.plot js/jQuery
                    (js/jQuery ".flot-timeline")
                    (clj->js the-data)
                    (clj->js (-> options
                                 (assoc :selection {:mode    "x"
                                                    :shape   "round"
                                                    :minSize 1
                                                    }))))]
    (if-let [{:keys [seriesIndex dataIndex]} (:point @!data)]
      (.highlight flot seriesIndex dataIndex))
    (.one (js/jQuery ".flot-timeline") "plotclick"
          (fn [_ _ item]
            (touch-data-to-force-rebind-click-handler !data)
            (if item
              (let [{:keys [datapoint dataIndex seriesIndex]} (js->clj item :keywordize-keys true)]
                (:selected-event (swap! !data assoc :selected-event (fa/event-at @!data seriesIndex dataIndex)))
                (swap! !data assoc :point {:datapoint datapoint :dataIndex dataIndex :seriesIndex seriesIndex}))
              (swap! !data dissoc :point :selected-event))))

    (.one (js/jQuery ".flot-timeline") "plotselected"
          (fn [_ ranges]
            (let [{{:keys [from to]} :xaxis} (js->clj ranges :keywordize-keys true)]
              (swap! !data (fn [data] (-> data
                                          (assoc-in [:timespan :selected-from] (tc/from-long from))
                                          (assoc-in [:timespan :selected-to] (tc/from-long to))))))))

    ))

(defn draw-with [!data]
  (let []
    (draw-selector !data (fa/selector-data-points @!data) (options {:xaxis (fa/selector-x-axis @!data)
                                                                    :yaxis (fa/selector-y-axis @!data)}))
    (draw-graph !data (fa/data-points @!data) (options {:xaxis (fa/x-axis @!data)
                                                        :yaxis (fa/y-axis @!data)}))))

(defn flot-component [!data]
  (fn []
    (reagent/create-class {:should-component-update (fn [& _] true)
                           :reagent-render          (fn [] [:span])
                           :component-did-mount     (fn [] (draw-with !data))
                           :component-did-update    (fn [] (draw-with !data))})))


(defn timeline-flot [!data]
  (fn []
    (let [results (:result @!data)]
      (when (not-empty results)
        [:div
         [graph-placeholder-with-description (r/cursor !data [:timespan])]
         [flot-component !data @!data]
         [se/selected-event !data]]))))
