(ns gov.stockport.sonar.visualise.ui.facet
  (:require [reagent.core :as r]))

(defn ->cs
  ([data] (->cs data {}))
  ([data cs]
   (r/atom {:facet-state cs
            :data        data})))

(defn checked? [!cs id]
  (get-in @!cs [:facet-state id]))

(defn- cb [!cs {:keys [id name count]}]
  ^{:key (gensym)}
  [:label
   [:input {:type      :checkbox
            :value     id
            :checked   (checked? !cs id)
            :on-change #(swap! !cs update-in [:facet-state id] not)}]
   (str name " (" count ")")])

(defn facet-tree [!cs]
  [:div.facet-tree
   (doall
     (map
     (fn [facet] (cb !cs facet))
     (:facets (:data @!cs))))])