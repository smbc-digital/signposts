(ns gov.stockport.sonar.visualise.util.stack)

(defn new-stack

  ([]
   (new-stack []))

  ([s & {:keys [value-when-empty]}]
   (let [!state (atom s)]
     {:push      (fn [new-item] (swap! !state #(conj % new-item)))
      :pop       (fn [] (let [head (first @!state)]
                          (swap! !state #(rest %))
                          (or head value-when-empty)))
      :is-empty? (fn [] (empty? @!state))})))

