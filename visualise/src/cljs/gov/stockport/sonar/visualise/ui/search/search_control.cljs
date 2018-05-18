(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]
            [gov.stockport.sonar.visualise.ui.search.search-history :refer [add-search-history!]]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state]]
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
  (.alert "Here")
  )

(defn search-criteria-control [query-callback]
  (scs/init! query-callback)
  (fn []
    [:div.container-fluid.py-1
     {:style {:background-color "#fff" :box-shadow "0px 10px 15px #ccc" :border-bottom "1px solid #ccc" :z-index "1000" :position "fixed" :width "100%" :height "55px" :top "50px"}}
     [:div.form-inline
      `[:span.py-1
        {:style {:display   :inline-flex
                 :flex-wrap :wrap}}
        ~@(map nugget (scs/search-criteria))]
      (if (> (count (scs/search-criteria)) 0)
       [:i.fa.fa-plus-circle
        {:aria-hidden "true"
         :style {
                 :color "#2A98EF"
                 :font-size "1.5em"
                 :margin-left "10px"
                 }
         :title "Add search criteria"
         :on-click
                      #(show-input-group)
         }

        ])
      [:div.input-group
       {:style
        {:margin-left "10px"}}
       [:select.custom-select.form-control.mr-2
       {
        :value     (scs/selected-control)
        :autoFocus "autofocus"
        :on-change #(scs/set-selected-field! (keyword (-> % .-target .-value)))}
       (map
         (fn [{:keys [target description]}]
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
           :on-key-up   #(when (= 13 (-> % .-keyCode)) (change-search-criteria))}]]])
       (if (not= "" (get-in qcs/query-types [(scs/selected-control) :placeholder] ))
       [:span.input-group-btn
        [:button.btn.btn-primary.search
         {:on-click change-search-criteria}
         "Search"]])]]]))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))