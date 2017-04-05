(ns gov.stockport.sonar.visualise.util.popper)

(defn poppable [s]
  (let [!state (atom s)]
    (fn []
      (let [head (first @!state)]
        (swap! !state #(rest %))
        head))))
