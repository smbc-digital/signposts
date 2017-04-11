(ns gov.stockport.sonar.visualise.util.blur)

(defn blurrer
  ([]
   (blurrer 0.05))
  ([amount]
   (fn [number number-of-distinct-values]
     (let [start (- number (* amount (/ (- number-of-distinct-values 1) 2)))]
       (map float (take number-of-distinct-values (range start 100 amount)))))))
