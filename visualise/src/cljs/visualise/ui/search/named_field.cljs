(ns visualise.ui.search.named-field)

(defn current-value [!state]
  [(keyword (:target @!state)) (:text @!state)])

(def options
  [{:target :name
    :desc   "Name"}
   {:target :name.keyword
    :desc   "Name-K"}
   {:target :address
    :desc   "Address"}
   {:target :address.keyword
    :desc   "Address-K"}])

(defn- search-target-selector [!state]
  [:select
   {:on-change #(swap! !state assoc :target (-> % .-target .-value))}
   (map
     (fn [{:keys [target desc]}]
       ^{:key target}
       [:option {:value    target
                 :selected (= target (:target @!state))} desc])
     options)])

(defn search-named-field [!state]
  (swap! !state assoc :target :name.keyword)
  (fn []
    [:div.form-group
     (search-target-selector !state)
     [:input {:type      :text
              :value     (second (current-value !state))
              :on-change #(swap! !state assoc :text (-> % .-target .-value))}]]))