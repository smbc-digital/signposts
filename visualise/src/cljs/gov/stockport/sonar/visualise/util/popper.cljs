(ns gov.stockport.sonar.visualise.util.popper)

(defn poppable

  ([]
   (poppable []))

  ([s & {:keys [value-when-empty]}]
   (let [!state (atom s)]
     (fn
       ([] (let [head (first @!state)]
             (swap! !state #(rest %))
             (or head value-when-empty)))
       ([new-item] (swap! !state #(conj % new-item)))))))
