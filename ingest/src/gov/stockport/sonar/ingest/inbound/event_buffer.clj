(ns gov.stockport.sonar.ingest.inbound.event-buffer)

(defn empty-buffer [{:keys [capacity]}]
  {:capacity capacity :qty 0 :events []})

(defn- full [{:keys [capacity qty]}]
  (>= qty capacity))

(defn create-buffer [{:keys [flush-fn] :as options}]
  (let [!buffer (atom (empty-buffer options))
        flusher (fn []
                  (flush-fn (:events @!buffer))
                  (reset! !buffer (empty-buffer options)))]
    {:flush flusher
     :queue (fn [event]
              (let [current (swap! !buffer #(-> %
                                                (update :qty inc)
                                                (update :events (fn [events] (conj events event)))))]
                (when (full current) (flusher))
                current))}))