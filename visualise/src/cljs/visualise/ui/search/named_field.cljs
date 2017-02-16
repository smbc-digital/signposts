(ns visualise.ui.search.named-field)

(defn current-value [!state]
  (:text @!state))

(defn search-named-field [!state]
  [:div
   [:input {:type      :text
            :value     (current-value !state)
            :on-change #(println (swap! !state assoc :text (-> % .-target .-value)))
            }]])