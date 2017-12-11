(ns gov.stockport.sonar.visualise.ui.results.timeline-flot
  (:require [reagent.core :as reagent]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljsjs.flot.plugins.navigate]
            [cljsjs.flot.plugins.resize]
            [cljs-time.coerce :as tc]
            [gov.stockport.sonar.visualise.ui.results.selected-event :as se]
            [gov.stockport.sonar.visualise.ui.results.flot-axes :as fa]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.util.keep-alive :refer [with-keep-alive]]
            [cljs-time.format :as f]))

(def !local (atom {}))

(defn options [m]
  (merge m {:grid   {:borderWidth     1
                     :minBorderMargin 20
                     :labelMargin     10
                     :hoverable       true
                     :clickable       true
                     :backgroundColor {:colors ["#ffffff" "#ffffff"]}
                     :margin          {:top    0
                                       :bottom 10
                                       :left   0
                                       :right  40}}
            :pan    {:interactive true
                     :cursor      :move
                     :frameRate   20}
            :zoom   {:interactive false
                     :cursor      :move}
            :lines  {:show false}
            :points {:radius 8}
            :legend {:show false}}))

(defn fmt [timestamp]
  (if timestamp
    (f/unparse (f/formatter "d MMMM YYYY") timestamp)))

(defn alternative-graph-controls []
  [:div.graph-controls
   [:i.fa.fa-2x.fa-arrow-left.pan-left]
   [:i.fa.fa-2x.fa-search-plus.zoom-in]
   [:i.fa.fa-2x.fa-search-minus.zoom-out]
   [:i.fa.fa-2x.fa-arrow-right.pan-right]])

(defn graph-placeholder [!data]
  [:div.graph
   [:div.row.mt-2 {:style {:margin-bottom "-20px"}}
    [:div.column.showing.col.ml-2
     [:div {:style {:font-weight "600" :font-size "1.2em"}}
     "Viewing "
     [:span.from ""]
     " to "
     [:span.to ""]]]]

   [:div.flot-timeline-container
    [alternative-graph-controls]
    [:div.flot-timeline]]])

(defn current-display-range [flot]
  (let [
        {:keys [min max]} (-> (.getOptions flot)
                              (aget "xaxes")
                              first
                              (js->clj :keywordize-keys true))]
    {:displayed-from (tc/from-long min)
     :displayed-to   (tc/from-long max)}))

(defn update-displayed-date-range [flot]
  (let [{:keys [displayed-from displayed-to]} (current-display-range flot)]
    (-> (js/jQuery ".showing .from")
        (.html (fmt displayed-from)))
    (-> (js/jQuery ".showing .to")
        (.html (fmt displayed-to)))))

(defn options-preserving-pan-and-zoom [search-uuid options]
  (if (and (:flot @!local) (= search-uuid (:search-uuid @!local)))
    (let [{:keys [displayed-from displayed-to]} (current-display-range (:flot @!local))]
      (-> options
          (assoc-in [:xaxis :min] displayed-from)
          (assoc-in [:xaxis :max] displayed-to)))
    options))

(defn on-plot-click-fn [!data event-map]
  (fn [_ _ item]
    (if item
      (let [seriesIndex (js->clj (aget item "seriesIndex"))
            dataIndex (js->clj (aget item "dataIndex"))]
        (swap! !data people/toggle-event (fa/event-at event-map seriesIndex dataIndex)))
      (swap! !data people/deselect-event))))

(defn on-event [selector events handler-fn]
  (-> (js/jQuery selector)
      (.off events)
      (.on events handler-fn)))

(defn on-click [selector on-click-fn]
  (on-event selector "click" on-click-fn))

(defn draw-graph [!data {:keys [flot-data event-map]} options]

  (reset! !local {:search-uuid (:search-uuid @!data)
                  :flot        (.plot js/jQuery
                                      (js/jQuery ".flot-timeline")
                                      (clj->js flot-data)
                                      (clj->js (options-preserving-pan-and-zoom (:search-uuid @!data) options)))})

  (let [flot (:flot @!local)]

    (update-displayed-date-range flot)

    (if-let [{:keys [seriesIndex dataIndex]} (fa/position-for event-map (:selected-event @!data))]
      (.highlight flot seriesIndex dataIndex))

    (on-event ".flot-timeline" "plotclick" (on-plot-click-fn !data event-map))

    (on-event ".flot-timeline" "plotpan plotzoom" (with-keep-alive (fn [& _] (update-displayed-date-range flot))))

    (on-click ".graph-controls .zoom-in" (fn [& _] (.zoom flot)))

    (on-click ".graph-controls .zoom-out" (fn [& _] (.zoomOut flot)))

    (on-click ".graph-controls .pan-left"
              (fn [& _]
                (.pan flot (clj->js {:left -20}))))

    (on-click ".graph-controls .pan-right"
              (fn [& _]
                (.pan flot (clj->js {:left 20}))))))

(defn draw-with [!data]
  (let []
    (draw-graph !data (fa/data-points @!data) (options {:xaxis (fa/x-axis @!data)
                                                        :yaxis (fa/y-axis @!data)}))))

(defn flot-component [!data]
  (fn []
    (reagent/create-class {:should-component-update (fn [& _] true)
                           :reagent-render          (fn [] [:span])
                           :component-did-mount     (with-keep-alive (fn [] (draw-with !data)))
                           :component-did-update    (with-keep-alive (fn [] (draw-with !data)))})))

(defn timeline-flot [!data]
  (fn []
    (let [results (:people @!data)]
      (when (not-empty results)
        [:div
         [graph-placeholder !data]
         [flot-component !data @!data]
         [se/selected-event !data]]))))