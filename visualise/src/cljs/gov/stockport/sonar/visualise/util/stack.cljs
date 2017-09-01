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

(defn new-colour-manager [available-keys]
  (let [!key-to-val (atom {})
        values-matching (fn [some-val] (fn [[_ v]] (= v some-val)))
        next-keys (fn [] (remove #(contains? @!key-to-val %) available-keys))]
    {:assign     (fn [some-val]
                   (if-let [next-key (first (next-keys))]
                     (do
                       (swap! !key-to-val assoc next-key some-val)
                       next-key)))
     :lookup     (fn [some-val]
                   (ffirst (filter (values-matching some-val) @!key-to-val)))
     :release    (fn [some-val]
                   (swap! !key-to-val
                          (fn [key-to-val] (reduce merge {} (remove (values-matching some-val) key-to-val)))))
     :available? (fn [] (not (empty? (next-keys))))}))

