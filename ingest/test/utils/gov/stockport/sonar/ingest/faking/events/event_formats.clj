(ns gov.stockport.sonar.ingest.faking.events.event-formats)

(defn- base-details [timeline]
  (let [basic-event (select-keys timeline [:name :dob :timestamp :event-source :event-type :address])]
    (-> basic-event
        (assoc :event-source (name (:event-source basic-event)))
        (assoc :event-type (name (:event-type basic-event))))))

(defn- duration [{:keys [duration]}]
  {:duration duration})

(defn- keyworker-details [{{:keys [name phone]} :keyworker}]
  {:keyworker-name  (:full-name name)
   :keyworker-phone phone})

; to allow re-definition of the defmulti on reload
(ns-unmap *ns* 'format-event)

(defmulti format-event
          (fn [timeline]
            (select-keys timeline [:event-source :event-type])))

; SCHOOLS

(defn- schools-data [{{{:keys [name phone district address headteacher]} :school} :meta}]
  {:school-name        name
   :school-phone       phone
   :school-district    (clojure.core/name district)
   :school-postcode    (:postcode address)
   :school-headteacher (get-in headteacher [:name :full-name])})

(defmethod format-event {:event-source :SCHOOLS :event-type :AWOL}
  [timeline]
  (merge (base-details timeline)
         (schools-data timeline)
         (duration timeline)))

(defmethod format-event {:event-source :SCHOOLS :event-type :EXCLUSION}
  [timeline]
  (merge (base-details timeline)
         (schools-data timeline)))

; EIS

(defn- eis-event [timeline]
  (merge (base-details timeline)
         (duration timeline)
         (select-keys timeline [:primary-presenting-issue :unique-pupil-number])))

(defmethod format-event {:event-source :EIS :event-type :CIN} [timeline] (eis-event timeline))

(defmethod format-event {:event-source :EIS :event-type :LAC} [timeline] (eis-event timeline))

(defmethod format-event {:event-source :EIS :event-type :CONTACT} [timeline] (eis-event timeline))

(defmethod format-event {:event-source :EIS :event-type :SEN} [timeline] (eis-event timeline))

; HOMES

(defn- home-event [timeline]
  (merge (base-details timeline)
         (select-keys timeline [:nino :keyworker])))

(defmethod format-event {:event-source :HOMES :event-type :ASB} [timeline] (home-event timeline))
(defmethod format-event {:event-source :HOMES :event-type :ARREARS-6-WK} [timeline] (home-event timeline))
(defmethod format-event {:event-source :HOMES :event-type :EVICTION-APPLICATION} [timeline] (home-event timeline))
(defmethod format-event {:event-source :HOMES :event-type :NOTICE-SEEKING-POSSESSION} [timeline] (home-event timeline))



(defmethod format-event :default [timeline] (base-details timeline))