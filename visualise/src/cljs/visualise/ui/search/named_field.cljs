(ns visualise.ui.search.named-field)

(defn current-value [!state]
  [(keyword (:target @!state)) (:text @!state)])

(def options
  [{:target :name
    :desc   "Name"}
   {:target :address
    :desc   "Address"}
   {:target :event-source
    :desc   "Source"}
   {:target :event-type
    :desc   "Type"}
   ])

(defn- search-target-selector [!state]
  [:select
   {:value     (:target @!state)
    :on-change #(swap! !state assoc :target (-> % .-target .-value))}
   (map
     (fn [{:keys [target desc]}]
       ^{:key target}
       [:option {:value target} desc])
     options)])

(defn search-named-field [!state]
  (swap! !state assoc :target (:target (first options)))
  (fn []
    [:span
     (search-target-selector !state)
     [:input {:type      :text
              :value     (second (current-value !state))
              :on-change #(swap! !state assoc :text (-> % .-target .-value))}]]))