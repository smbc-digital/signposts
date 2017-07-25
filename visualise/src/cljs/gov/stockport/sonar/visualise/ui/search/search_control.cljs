(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]))

(defonce !search-control-state (r/atom {}))

(defn nugget [{:keys [selected-control search-term] :as item}]
  [:div.input-group.nugget.mr-2
   [:span.input-group-addon.name (name selected-control)]
   [:span.input-group-addon.val search-term
    [:i.fa.fa-times.ml-2
     {:on-click #(scs/remove-search-criteria! !search-control-state item)}]]])


(defn search-criteria-control [query-callback]
  (scs/init! !search-control-state query-callback)
  (fn []
    [:div.container-fluid.py-1
     {:style {:background-color "#1d2932"}}
     [:div.form-inline
      [:select.custom-select.form-control.mr-2
       {:value     (scs/selected-control !search-control-state)
        :autoFocus "autofocus"
        :on-change #(scs/set-selected-field! !search-control-state (keyword (-> % .-target .-value)))}
       (map
         (fn [{:keys [target description]}]
           ^{:key target}
           [:option {:value target} description])
         qcs/options)]
      [:div.input-group
       [:input.form-control {:value       (scs/search-term !search-control-state)
                             :placeholder (get-in qcs/query-types [(scs/selected-control !search-control-state) :placeholder])
                             :on-change   #(scs/set-search-term! !search-control-state (-> % .-target .-value))
                             :on-key-up   #(when (= 13 (-> % .-keyCode)) (scs/add-search-criteria! !search-control-state))}]
       [:span.input-group-btn
        [:button.btn.btn-success.mr-2
         {:on-click #(scs/add-search-criteria! !search-control-state)}
         [:i.fa.fa-search]]]]
      `[:span.py-1
        {:style {:display   :inline-flex
                 :flex-wrap :wrap}}
        ~@(map nugget (scs/search-criteria !search-control-state))]]]))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))

