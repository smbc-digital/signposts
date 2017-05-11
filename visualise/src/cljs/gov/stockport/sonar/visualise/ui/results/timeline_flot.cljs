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
                     :backgroundColor {:colors ["#fff" "#e4f4f4"]}
                     :margin          {:top    10
                                       :bottom 10
                                       :left   10}}
            :pan    {:interactive true
                     :cursor      :move
                     :frameRate   20}
            :zoom   {:interactive true
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
   (let [{:keys [show-only-highlighted? show-only-highlighted-disabled?]} @!data]
     [:div.highlight-control
      (when show-only-highlighted-disabled? {:class "disabled"})
      [:i.fa.fa-2x.pull-left
       {:class    (if show-only-highlighted? "fa-toggle-on" "fa-toggle-off")
        :on-click (with-keep-alive #(swap! !data people/toggle-show-only-highlighted))}]
      [:p.info "Show highlighted individuals only"]])

   [:div.showing
    [:center
     [:span "from "]
     [:strong.from ""]
     [:span " to "]
     [:strong.to ""]]]
   [:div.flot-timeline-container
    [alternative-graph-controls]
    [:div.flot-timeline {:style {:width "100%" :height 500}}]]])

(defn current-display-range [flot]
  (let [{:keys [min max]} (-> (.getOptions flot)
                              .-xaxes
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

(defn draw-graph [!data {:keys [data event-map]} options]

  (reset! !local {:search-uuid (:search-uuid @!data)
                  :flot        (.plot js/jQuery
                                      (js/jQuery ".flot-timeline")
                                      (clj->js data)
                                      (clj->js (options-preserving-pan-and-zoom (:search-uuid @!data) options)))})

  (let [flot (:flot @!local)]

    (update-displayed-date-range flot)

    (if-let [{:keys [seriesIndex dataIndex]} (fa/position-for event-map (:selected-event @!data))]
      (.highlight flot seriesIndex dataIndex))

    (.bind (js/jQuery ".flot-timeline") "plotclick"
           (fn [_ _ item]
             (when item
               (let [{:keys [datapoint seriesIndex dataIndex]} (js->clj item :keywordize-keys true)]
                 (:selected-event
                   (swap! !data #(-> %
                                     (assoc :selected-event (fa/event-at event-map seriesIndex dataIndex)))))
                 (swap! !data assoc :point {:datapoint datapoint :dataIndex dataIndex :seriesIndex seriesIndex})))))

    (.bind (js/jQuery ".flot-timeline") "plotpan plotzoom" (with-keep-alive (fn [& _] (update-displayed-date-range flot))))

    (.bind (js/jQuery ".graph-controls .zoom-in") "click"
           (fn [& _]
             (.zoom flot)))

    (.bind (js/jQuery ".graph-controls .zoom-out") "click"
           (fn [& _]
             (.zoomOut flot)))

    (.bind (js/jQuery ".graph-controls .pan-left") "click"
           (fn [& _]
             (.pan flot (clj->js {:left 20}))))

    (.bind (js/jQuery ".graph-controls .pan-right") "click"
           (fn [& _]
             (.pan flot (clj->js {:left -20}))))))

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
    (let [results (:result @!data)]
      (when (not-empty results)
        [:div
         [graph-placeholder !data]
         [flot-component !data @!data]
         [se/selected-event !data]]))))