(ns visualise.common.query.aggregate)

(defn with-term-aggregation [qip & terms]
  (merge
    qip
    (let [work-inside-out (reverse terms)]
      (loop [remaining-terms work-inside-out
             query {}]
        (if (not-empty remaining-terms)
          (let [term (first remaining-terms)
                inner (if (:aggs query)
                        {:aggs {term {:terms {:field term :size 100}
                                      :aggs  (:aggs query)}}}
                        {:aggs {term {:terms {:field term :size 100}}}})]
            (recur (rest remaining-terms) inner))
          query)))))

