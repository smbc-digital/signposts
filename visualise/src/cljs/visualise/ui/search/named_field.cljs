(ns visualise.ui.search.named-field)

(defn search-named-field [!state]
  [:div
   [:input {:type      :text
            :value     (:text @!state)
            :on-change #(swap! !state assoc :text (-> % .-target .-value))
            }]])