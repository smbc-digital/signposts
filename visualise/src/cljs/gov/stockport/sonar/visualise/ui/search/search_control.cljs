(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]
            [gov.stockport.sonar.visualise.state
               :refer
             [!search-control-state !data !search-type !show-select !show-input   !active-plus]]))


(defn- show-dropdown! []
  (reset! !show-select 1))

(defn- hide-dropdown! []
  (reset! !show-select 0))

(defn- show-search-item! []
  (reset! !show-input 1))

(defn- hide-search-item! []
  (reset! !show-input 0)
  )

(defn activate-plus[]
  (reset! !active-plus 1))

(defn deactivate-plus[]
  (reset! !active-plus 0))


(defn- hide-search-field[]
  (show-dropdown!)
  (hide-search-item!))

(defn- toggle-view[field]
  (if (= :none field)
    (do
    (show-dropdown!)
    (hide-search-item!))
    (do
      (hide-dropdown!)
      (show-search-item!)
      (scs/set-selected-field! field))))

(defn- change-search-criteria[]
  (scs/add-search-criteria!)
  (add-search-history!))

(defn nugget [{:keys [query-type search-term]}]
  ^{:key (gensym)}
  [:div.search-item
   [:div.item-container
   [:label  {:style {:width "100%"}} (name query-type)
    [:input
     {:type "text"
      :value search-term
      :name (name query-type)
      :id (name query-type)
      :size "15"
      :read-only "true"}]]]
     [:div.delete-item-container
     [:i.fa.fa-times.ml-2.delete-item
     {:on-click #(scs/remove-search-criteria! query-type)}]]])

(defn show-input-group []
  (scs/add-search-criteria!)
  )

(defn input-group[]
  [:div.input-group
   {:style {:margin-left "10px"}}
   (when (> @!show-select 0)
     [:select.custom-select.form-control.mr-2
      {
       :value     "none"
       :autoFocus "autofocus"
       :on-change #(toggle-view (keyword (-> % .-target .-value)))
       }
      (map
        (fn [{:keys [target description selected]}]
          ^{:key target}
          [:option.option {:value target}  description])
        (sort-by :display-order qcs/options))]
     )
   (when (= 1 @!show-input)
     [:div.search-item
      [:div.item-container
      [:label {:style {:width "100%"}}
        (get-in qcs/query-types [(scs/selected-control) :placeholder])
       [:input
        {:type (get-in qcs/query-types [(scs/selected-control) :placeholder])
         :value  (scs/search-term)
         :name "search-term"
         :id "search-term"
         :size "10"
         :on-change   #(scs/set-search-term! (-> % .-target .-value))
         :on-key-up   #(when (= 13 (-> % .-keyCode)) (change-search-criteria))}]]]
      [:div.delete-item-container
       [:i.fa.fa-times.ml-2.delete-item
        {:on-click  hide-search-field}]]])])


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
      (if (or
            (> (count (scs/search-criteria)) 0)
            (=  0 !show-select ))
        [:i.fa.fa-plus-circle.add-search-item.active
         {
          :title       "Add search criteria"
          :on-click    show-dropdown!}
         ]
        [:i.fa.fa-plus-circle.add-search-item])
        [:span.input-group-btn
         [:button.btn.btn-primary.search
          {:on-click change-search-criteria}"Search"]]]]))


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