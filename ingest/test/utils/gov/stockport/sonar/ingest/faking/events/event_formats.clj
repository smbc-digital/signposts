(ns gov.stockport.sonar.ingest.faking.events.event-formats)

(defn base-details [timeline]
  (let [basic-event (select-keys timeline [:name :dob :timestamp :event-source :event-type :address])]
    (-> basic-event
        (assoc :event-source (name (:event-source basic-event)))
        (assoc :event-type (name (:event-type basic-event))))))

(defn duration [{:keys [duration]}]
  {:duration duration})

(defn keyworker-details [{{:keys [name phone]}:keyworker}]
  {:keyworker-name (:full-name name)
   :keyworker-phone phone})

(defn schools-data [{{{:keys [name phone district address headteacher]} :school} :meta}]
  {:school-name        name
   :school-phone       phone
   :school-district    (clojure.core/name district)
   :school-postcode    (:postcode address)
   :school-headteacher (get-in headteacher [:name :full-name])})

(defmulti format-event
          (fn [timeline]
            (select-keys timeline [:event-source :event-type])))

(defmethod format-event {:event-source :SCHOOLS :event-type :AWOL}
  [timeline]
    (merge (base-details timeline)
           (schools-data timeline)
           (duration timeline)))

(defmethod format-event {:event-source :SCHOOLS :event-type :EXCLUSION}
  [timeline]
  (merge (base-details timeline)
         (schools-data timeline)))

(defmethod format-event {:event-source :EIS :event-type :CIN}
  [timeline]
  (merge (base-details timeline)
         (duration timeline)
         (keyworker-details timeline)))

(defmethod format-event {:event-source :EIS :event-type :LIC}
  [timeline]
  (merge (base-details timeline)
         (duration timeline)
         (keyworker-details timeline)))

(defmethod format-event :default
  [timeline]
  (base-details timeline))