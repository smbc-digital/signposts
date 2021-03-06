(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]
            [gov.stockport.sonar.visualise.ui.search.input-types :as it]
            [gov.stockport.sonar.visualise.state
               :refer
             [!search-control-state !data !search-type !show-select !show-input
              !active-plus !selected-options]]))


(defn- show-dropdown! []
  "Shows DropDown"
  (reset! !show-select 1))

(defn- hide-dropdown! []
  "Hides Dropdown"
  (reset! !show-select 0))

(defn- show-search-item! []
  "Shows Text Field"
  (reset! !show-input 1))

(defn- hide-search-item! []
  "Hides Text Field"
  (reset! !show-input 0))

(defn activate-plus![]
  "Activates Plus Item"
  (reset! !active-plus 1))

(defn deactivate-plus![]
  "Deactivates Plus Sign"
  (reset! !active-plus 0))

(defn- hide-search-field[]
  "Hides text input and shows Select"
  (show-dropdown!)
  (hide-search-item!))

(defn- toggle-view[field]
  "Shows text field if search field is selected"
  (if (= "none" field)
    (do
      (show-dropdown!)
      (hide-search-item!)
      (deactivate-plus!)
      )
    (do
      (hide-dropdown!)
      (activate-plus!)
      (show-search-item!)
      (scs/set-selected-field! field)
      (swap! !selected-options conj field))))

(defn- remove-search-criteria[query-type]
  (swap! !selected-options disj query-type)
  (scs/remove-search-criteria! query-type))

(defn- set-search-term[value]
  (scs/set-search-term! value))

(defn show-input-group []
  (scs/add-search-criteria!))

(defn- change-search-criteria[]
  (scs/add-search-criteria!)
  (hide-search-field)
  (show-dropdown!)
  (deactivate-plus!))

(defn change-search-criteria-and-search[]
  (scs/add-search-criteria-and-search!)
  (hide-search-field)
  (show-dropdown!)
  (add-search-history!))

(defn- nugget [{:keys [query-type search-term]}]
  ^{:key (gensym)}
  [:div.search-item
   [:div.item-container
   [:label  {:style {:width "100%"}} (name query-type)
    [:input
     {:type "text"
      :default-value search-term
      :name (name query-type)
      :id (name query-type)
      :size "15"
      :on-change  #(scs/set-search-term! (-> % .-target .-value))
      :on-key-up  #(when (= 13 (-> % .-keyCode)) (change-search-criteria-and-search))
      }]]]
     [:div.delete-item-container
     [:i.fa.fa-times.ml-2.delete-item
     {:on-click #(remove-search-criteria query-type)}]]])

(defn reset-search-field[]
  (let [selected-control (scs/selected-control)]
  (swap! !selected-options disj selected-control)
  (hide-search-field)
  (swap! !search-control-state assoc :search-term "")))


(defn text-input[]
  [:input
   {:type "text"
    :default-value  (scs/search-term)
    :name "search-term"
    :id "search-term"
    :size (get-in qcs/query-types [(scs/selected-control) :size])}])



(defn input-group[]
  [:div.input-group
   {:style {:margin-left "10px"}}
   (when (> @!show-select 0)
     [:select.custom-select.form-control.mr-2
      {
       :value     "none"
       :autoFocus "autofocus"
       :on-change #(toggle-view (keyword (-> % .-target .-value)))}
      (when (empty? (scs/search-criteria))
        (reset! !selected-options  #{}) )
      (let [foo  @!selected-options]
      (map
        (fn [{:keys [target description selected]}]
          (when (not(contains? foo target))
          ^{:key target}
          [:option.option {:value target}  description]))
          (sort-by :display-order qcs/options)))])
   (when (= 1 @!show-input)
     [:div.search-item
      [:div.item-container
      [:label {:style {:width "100%"}}
        (get-in qcs/query-types [(scs/selected-control) :placeholder])
        [it/get-input (get-in qcs/query-types [(scs/selected-control) :input-type])]
       ]]
      [:div.delete-item-container
       [:i.fa.fa-times.ml-2.delete-item
        {:on-click  reset-search-field}]]])])

(defn search-criteria-control [query-callback]
  (scs/init! query-callback)
  (fn []
    [:div.container-fluid.py-1.search-criteria-control
     [:div.form-inline
      `[:span.py-1
        {:style {:display   :inline-flex
                 :flex-wrap :wrap}}
        ~@(map nugget (scs/search-criteria))]
          [input-group]
      (if (= @!active-plus 0)
         [:i.fa.fa-plus-circle.add-search-item]
         [:i.fa.fa-plus-circle.add-search-item.active
         {:title       "Add search criteria"
          :on-click    change-search-criteria}])
        [:span.input-group-btn
         [:button.btn.btn-primary.search
          {:on-click change-search-criteria-and-search}"Search"]]]]))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    (hide-search-item!)
    (show-dropdown!)
    [search-criteria-control query-callback]))