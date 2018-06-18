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

(defmulti
  format-event
  (fn [timeline]
    (case (select-keys timeline [:event-source])
      {:event-source :SCHOOLS} (if (= (:event-type timeline) :AWOL) :schools-awol :schools)
      {:event-source :SCHOOLS :event-type :EXCLUSION} :schools-exclusion
      {:event-source :EIS} :eis
      {:event-source :HOMES} :homes
      :default)))

; SCHOOLS

(defn- schools-data [{{{:keys [name phone district address headteacher]} :school} :meta}]
  {:school-name        name
   :school-phone       phone
   :school-district    (clojure.core/name district)
   :school-postcode    (:postcode address)
   :school-headteacher (get-in headteacher [:name :full-name])})

(defmethod format-event :schools-awol
  [timeline]
  (merge (base-details timeline)
         (schools-data timeline)
         (duration timeline)))

(defmethod format-event :schools
  [timeline]
  (merge (base-details timeline)
         (schools-data timeline)))

; EIS

(defn- eis-event [timeline]
  (merge (base-details timeline)
         (duration timeline)
         (select-keys timeline [:primary-presenting-issue :unique-pupil-number])))

(defmethod format-event :eis [timeline] (eis-event timeline))

; HOMES

(defn- home-event [timeline]
  (merge (base-details timeline)
         (select-keys timeline [:nino :keyworker])))

(defmethod format-event :homes [timeline] (home-event timeline))

(defmethod format-event :default [timeline] (base-details timeline))


;YOS
(defn- yos-event[timeline]
       (merge base-details)


       )

