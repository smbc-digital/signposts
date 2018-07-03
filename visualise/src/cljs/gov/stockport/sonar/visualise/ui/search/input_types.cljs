(ns gov.stockport.sonar.visualise.ui.search.input-types
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.state :refer [!status]]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh] ))

(def event-sources (r/atom  (set [])))

(defn- map-event-sources[{:keys [event-source]}]
  (when (not (contains? @event-sources event-source))
    (swap! event-sources conj event-source)))

(defn- option-event-source[event-source]
    ^{:key (gensym)}
    [:option {:value event-source} event-source])

(defn- option-event-type[{:keys [event-type]}]
  ^{:key (gensym)}
  [:option {:value event-type} (fh/-label event-type)])

(defn event-source[]
  [:select.event-source
   {
    :value (or (scs/search-term) "")
    :name "seach-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))}
   [:option {:value "" :selected :selected} "Please select ..."]
    (map map-event-sources (sort-by :event-source @!status))
    (map option-event-source @event-sources)])

(defn event-type[]
  [:select.event-type
   {
    :value (or(scs/search-term) "")
    :name "seach-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))}
   [:option {:value "" :selected :selected} "Please select ..."]
   (map option-event-type (sort-by :event-source @!status))
   ])

(defn text-input[]
  [:input
   {:type "text"
    :value  (scs/search-term)
    :name "search-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))}])

(def input-map
  {:text-input text-input
   :event-source event-source
   :event-type event-type})

(defn get-input [input-type]
  (input-type input-map text-input))