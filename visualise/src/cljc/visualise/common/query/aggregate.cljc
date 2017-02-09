(ns visualise.common.query.aggregate)

(defn query []
  {})

(defn with-no-results [qip]
  (assoc qip :size 0))

(defn with-size [qip number]
  (assoc qip :size number))

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

(defn with-query-string [qip qs]
  (update-in
    qip
    [:query :bool :must]
    #(into [] (flatten (filter not-empty (cons (or % []) [{:query_string {:query         qs
                                                                          :default_field "_all"}}]))))))

(defn with-max-age [qip max-age]
  (update-in
    qip
    [:query :bool :must]
    #(into [] (flatten (filter not-empty (cons (or % []) [{:range {:dob {:gte (str "now-" max-age "y")}}}]))))))
