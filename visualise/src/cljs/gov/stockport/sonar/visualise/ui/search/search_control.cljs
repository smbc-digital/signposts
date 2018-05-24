(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state !data]]
            [clojure.string :as str]))

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
   {:style
    {:margin-left "10px"}}
   [:select.custom-select.form-control.mr-2
    {
     :value     (scs/selected-control)
     :autoFocus "autofocus"
     :default-value "none"
     :on-change #(scs/set-selected-field! (keyword (-> % .-target .-value)))}

    (map
      (fn [{:keys [target description selected]}]
        ^{:key target}
        [:option {:value target} (str/upper-case description)])
      (sort-by :display-order qcs/options))]

   (if (not= "" (get-in qcs/query-types [(scs/selected-control) :placeholder] ))

     [:div.search-event-item
      [:label
       {:style {:width "100%"}}  (get-in qcs/query-types [(scs/selected-control) :placeholder])

       [:input
        {:type (get-in qcs/query-types [(scs/selected-control) :placeholder])
         :value  (scs/search-term)
         :name "search-term"
         :id "search-term"
         :size "15"
         :on-change   #(scs/set-search-term! (-> % .-target .-value))
         :on-key-up   #(when (= 13 (-> % .-keyCode)) (change-search-criteria))}]]])])


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
      [:i.fa.fa-plus-circle.add-search-item
       {
        :aria-hidden "true"
        :title "Add search criteria"
        :on-click change-search-criteria}
       ]

        [:span.input-group-btn
         [:button.btn.btn-primary.search
          {:on-click change-search-criteria}
          "Search"]]
      ]]))


(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {})
    )))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))