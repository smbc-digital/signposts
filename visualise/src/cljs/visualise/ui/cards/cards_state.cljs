(ns visualise.ui.cards.cards-state)

(defn map-results [!data]
  (let [results (:result @!data)]
    (set (map (fn [x] (dissoc x :timestamp :event-source :event-type)) results))))

(defn print-results [!data]
  (fn[]
    (let [uniq-data (map-results !data)]
      [:div.panel.panel-default.criteria-box
       [:div.panel-heading "Cards"]
       [:div.panel-body
        (map (fn [x]
               ^{:key (gensym)}
               [:div.panel.panel-default.criteria
                [:div.panel-heading (get x :name)
                 [:div.panel-body
                  [:p "DOB: "(get x :dob)]
                  [:p "ADDRESS: "(get x :address)]]]]) uniq-data)]])))


