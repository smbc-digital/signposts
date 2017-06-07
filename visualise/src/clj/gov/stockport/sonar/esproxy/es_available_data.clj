(ns gov.stockport.sonar.esproxy.es-available-data)

(def data-freshness-aggregation-query
  {:size 0
   :aggs {:event-source
          {:terms {:field "event-source.keyword" :size 100}
           :aggs  {:event-type
                   {:terms {:field "event-type.keyword" :size 100}
                    :aggs  {:timestamp-from {:min {:field "timestamp"}}
                            :timestamp-to   {:max {:field "timestamp"}}
                            :loaded         {:max {:field "ingestion-timestamp"}}}}}}}})


(defn summarise-available-data [result]
  (flatten
    (map
      (fn [{event-source :key :as source}]
        (map
          (fn [{event-type :key qty :doc_count :keys [loaded timestamp-from timestamp-to]}]
            {:event-source event-source
             :event-type   event-type
             :qty          qty
             :from         (:value_as_string timestamp-from)
             :to           (:value_as_string timestamp-to)
             :last-updated (:value_as_string loaded)})
          (get-in source [:event-type :buckets])))
      (get-in result [:aggregations :event-source :buckets]))))