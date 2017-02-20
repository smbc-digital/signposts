(ns visualise.ui.search.named-field)

(defn current-value [!state]
  (:text @!state))

(defn search-named-field [!state]
  [:div.form-group
   [:label "Name"]
   [:input {:type      :text
            :value     (current-value !state)
            :on-change #(swap! !state assoc :text (-> % .-target .-value))}]])