(ns ingest.core
  (:require [clojure.java.io :as io]
            [clojure-csv.core :as csv]
            [semantic-csv.core :as sc]
            [clojure.set]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]))

(defn load-csv [filename]
  (sc/slurp-csv filename))

(defn load-csv-as-hashmap-over-key [filename key]
  (let [row (sc/slurp-csv filename)]
    (zipmap (map #(get % key) row) row)))


(defn conjoin-exclusions-to-students-and-schools []
  (let [exclusions (sc/slurp-csv "c:\\DATA\\exclusions.csv")
        students (load-csv-as-hashmap-over-key "c:\\DATA\\students.csv" :stud_id)
        schools (load-csv-as-hashmap-over-key "c:\\DATA\\bases.csv" :base_id)]
    (map #(conj % (get students (get % :stud_id)) (get schools (get % :base_id))) exclusions)))

(defn exclusion-events-in-es-format []
  (let [events-with-relevant-fields-per-exclusion
        (map #(select-keys % [:KnownAs :dob :Start_Date :OFFICIAL_BASE_NAME :ncode_des])
             (conjoin-exclusions-to-students-and-schools))]
    (map #(clojure.set/rename-keys % {:Start_Date :timestamp, :KnownAs :name, :OFFICIAL_BASE_NAME :agency,
                                      :ncode_des  :agency-subtype
                                      }) events-with-relevant-fields-per-exclusion)))

(def date-format (f/formatter "dd/MM/yyyy"))

(defn date-time-formatter [datestring date-type]
  (f/unparse (date-type f/formatters) (f/parse (f/formatter "dd/MM/yyyy") datestring))
  )

(defn exclusion-events-in-es-format-with-event-source []
  (let [events-with-event-source (map #(conj % {:event-source :SCHOOLS
                                                :event-type   :EXCLUSION}) (exclusion-events-in-es-format))]
    (map #(update-in % [:dob] date-time-formatter :date)
         (map #(update-in % [:timestamp] date-time-formatter :date-time) events-with-event-source))))
