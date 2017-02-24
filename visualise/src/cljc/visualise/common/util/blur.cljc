(ns visualise.common.util.blur)

(defn blurred [number number-of-distinct-values]
  (let [start (- number (* 0.05 (/ (- number-of-distinct-values 1) 2)))]
    (map float (take number-of-distinct-values (range start 100 0.05)))))

