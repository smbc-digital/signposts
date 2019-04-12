(ns gov.stockport.sonar.visualise.ui.search.input-types
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]
            [gov.stockport.sonar.visualise.state :refer [!status !show-select !show-input]]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh] ))

(defn- show-dropdown! []
  "Shows DropDown"
  (reset! !show-select 1))

(defn- hide-search-item! []
  "Hides Text Field"
  (reset! !show-input 0))

(defn- hide-search-field[]
  "Hides text input and shows Select"
  (show-dropdown!)
  (hide-search-item!))

(defn- option-event-source[events]
    (let [event-source (key events)]
    ^{:key (gensym)}
    [:option {:value event-source} event-source]))

(defn- option-event-type[{:keys [event-type]}]
  ^{:key (gensym)}
   [:option {:value event-type} (fh/-label event-type)])

(defn- optgroup-event-type[events]
  (let [event-source (key events)]
    ^{:key (gensym)}
    [:optgroup {:label event-source}
    (map option-event-type (sort-by :event-type(val events)))]))

(defn change-search-criteria-and-search[]
  (scs/add-search-criteria-and-search!)
  (hide-search-field)
  (show-dropdown!)
  (add-search-history!))

(defn event-source[]
  [:select.event-source
   {
    :value (or (scs/search-term) "")
    :name "search-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))}
   [:option {:value "" :selected :selected} "Please select ..."]
    (map option-event-source (sort (group-by :event-source @!status)))])

(defn event-type[]
  [:select.event-type
   {
    :value (or (scs/search-term) "")
    :name "search-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))}
   [:option {:value "" :selected :selected} "Please select ..."]
    (map optgroup-event-type (sort (group-by :event-source @!status)))])

(defn text-input[]
  [:input
   {:type "text"
    :value  (scs/search-term)
    :name "search-term"
    :id "search-term"
    :on-change   #(scs/set-search-term! (-> % .-target .-value))
    :on-key-down #(when (= 13 (-> % .-keyCode)) (change-search-criteria-and-search))}])

(def input-map
  {:text-input text-input
   :event-source event-source
   :event-type event-type})

(defn get-input [input-type]
  (input-type input-map text-input))