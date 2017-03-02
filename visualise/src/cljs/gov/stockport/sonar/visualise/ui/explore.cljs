(ns gov.stockport.sonar.visualise.ui.explore
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.aggregation.data :refer [random-events]]
            [gov.stockport.sonar.visualise.aggregation.aggregation :refer [aggregate-and-group]]
            [cljs-time.core :as t]))

(defn clj->json
  [ds]
  (.stringify js/JSON (clj->js ds)))

(defn dump [val]
  (println (clj->json val)))

(def view-state-defaults {:vb-w            1200
                          :vb-h            200
                          :vb-x            0
                          :vb-y            0
                          :lhs             250
                          :available-width 900
                          :zoom            1})

(defn view-state
  ([] (view-state {}))
  ([opts] (r/atom (merge view-state-defaults opts))))

(defn view-box [!view]
  (str/join " " (vals (select-keys @!view [:vb-x :vb-y :vb-w :vb-h]))))

(defn scaler [x y]
  (fn [val]
    (* (/ 1 (js/Math.log val)) (/ x (/ 1 (js/Math.log y))))))

(def bucket-font-scaler (scaler 14 56))                     ; 14 px font at 56 buckets

(defn scale [!view y !aggregated-event-series]
  (fn []
    (let [{:keys [lhs available-width zoom vb-x vb-y]} @!view
          {buckets-on-display :number-of-buckets} @!aggregated-event-series
          bucket-count-font (bucket-font-scaler buckets-on-display)
          bucket-w (* zoom (/ available-width buckets-on-display))
          line-w (* bucket-w buckets-on-display)
          bucket-w-offset-x (/ bucket-w 2)
          y (+ vb-y y)]
      [:g
       [:rect {:x lhs :y vb-y :width line-w :height 37 :fill :lightgrey}]
       (map
         (fn [{:keys [bucket-number bucket-name]}]
           (let [bucket-x1 (+ lhs (* bucket-number bucket-w))
                 bucket-x2 (+ bucket-x1 bucket-w)
                 bucket-mid-x (+ bucket-x1 bucket-w-offset-x)
                 {:keys [heading sub-heading]} bucket-name]
             ^{:key (gensym)}
             [:g
              [:line {:x1 bucket-x1 :x2 bucket-x2 :y1 y :y2 y :stroke :black}]

              [:text {:x bucket-mid-x :y (- y 8) :text-anchor :middle :alignment-baseline :middle :font-size (/ bucket-count-font 1.5)} heading]
              [:text {:x bucket-mid-x :y (+ y 8) :text-anchor :middle :alignment-baseline :middle :font-size (/ bucket-count-font 2)} sub-heading]
              [:line {:x1 bucket-x1 :x2 bucket-x1 :y1 (- y 3) :y2 (+ y 3) :stroke :black :stroke-width 2}]
              [:line {:x1 bucket-x2 :x2 bucket-x2 :y1 (- y 3) :y2 (+ y 3) :stroke :black :stroke-width 2}]
              ]))
         (:buckets @!aggregated-event-series))
       [:rect {:x 0 :y vb-y :width (+ vb-x (- lhs 1)) :height 37 :fill :white}]
       ])))

(defn series [!view y series-key !aggregated-event-series]
  (fn []
    (let [{:keys [lhs available-width zoom vb-x]} @!view
          {buckets-on-display :number-of-buckets
           buckets            :buckets} @!aggregated-event-series
          bucket-w (* zoom (/ available-width buckets-on-display))]
      [:g
       (map
         (fn [{:keys [bucket-number contents]}]
           (let [series-content (get contents series-key)
                 bucket-x1 (+ lhs (* bucket-number bucket-w))
                 bucket-x2 (+ bucket-x1 bucket-w)]
             ^{:key (gensym)}
             [:g
              [:line {:x1 bucket-x1 :x2 bucket-x2 :y1 y :y2 y :stroke :black}]
              [:g
               (map
                 (fn [{:keys [position-in-bucket] :as event}]
                   (let [pos (+ bucket-x1 (* position-in-bucket (- bucket-x2 bucket-x1)))]
                     ^{:key (gensym)}
                     [:line {:x1           pos :x2 pos :y1 (- y 4) :y2 (+ y 4)
                             :stroke       :black
                             :stroke-width 4}]))
                 series-content)]]))
         (sort-by :bucket-number buckets))
       [:rect {:x vb-x :y (- y 20) :width 250 :height 40 :fill :white}]
       [:text.key {:x (+ vb-x (- lhs 10)) :y y :text-anchor :end :alignment-baseline :middle :font-size 14} (str/join "-" series-key)]
       ])))

(def group-by-event-type (fn [event] [(:event-source event) (:event-type event)]))

(def dec-to-one (fn [x] (max 1 (dec x))))

(defn graph-controls [!view aggregation-fn]
  (fn []
    [:span
     [:input {:type      :range :min 0 :max 1000 :step 50
              :value     (:vb-x @!view)
              :on-change #(swap! !view assoc :vb-x (-> % .-target .-value int))}]
     [:input {:type      :range :min 0 :max 1000 :step 50
              :value     (:vb-y @!view)
              :on-change #(swap! !view assoc :vb-y (-> % .-target .-value int))}]
     [:i.fa.fa-search-plus {:on-click #(aggregation-fn (swap! !view update :zoom inc))}]
     [:i.fa.fa-search-minus {:on-click #(aggregation-fn (swap! !view update :zoom dec-to-one))}]]))

(defn graph-svg [!view event-series !aggregated-event-series]
  (fn []
    (let [event-types (sort (into #{} (map group-by-event-type event-series)))]
      [:div
       [:svg {:width                 "100%"
              :height                "100%"
              :view-box              (view-box !view)
              :preserve-aspect-ratio "xMinYMin meet"}

        (map
          (fn [[idx event-type]]
            ^{:key (gensym)} [series !view (+ 60 (* idx 30)) event-type !aggregated-event-series])
          (zipmap (range) event-types))
        [scale !view 20 !aggregated-event-series]
        ]])))

(defn aggregation-fn [event-series !aggregated-event-series]
  (fn [{:keys [zoom]}]
    (reset! !aggregated-event-series
            (aggregate-and-group event-series zoom group-by-event-type))))

(defn graph [!view event-series]
  (let [!aggregated-event-series (r/atom event-series)
        aggregator (aggregation-fn event-series !aggregated-event-series)]
    (aggregator @!view)
    (fn []
      [:div
       [graph-controls !view aggregator]
       [graph-svg !view event-series !aggregated-event-series]])))

(defn graphit [!view !data]
  (fn []
    (graph !view (:result @!data))))
