(ns gov.stockport.sonar.ingest.faking.events.event-formats)

(defmulti format-event
          (fn [timeline]
            (select-keys timeline [:event-source :event-type])))

;(defmethod format-event {:event-source :HOMES
;                         :event-type   :VISIT}
;  [timeline]
;  (println timeline)
;  timeline)

(defmethod format-event :default
  [timeline]
  (let [basic-event (select-keys timeline [:name :dob :timestamp :event-source :event-type :address])]
    (-> basic-event
        (assoc :event-source (name (:event-source basic-event)))
        (assoc :event-type (name (:event-type basic-event))))))