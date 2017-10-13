(ns gov.stockport.sonar.visualise.util.fmt-help
  (:require [clojure.string :as str]
            [cljs-time.format :as f]
            [cljs.pprint :refer [cl-format]]
            [camel-snake-kebab.core :refer [convert-case]]))

(def ellipsis \u2026)

(def ->Title-Case (partial convert-case clojure.string/capitalize clojure.string/capitalize " "))

(defn address-summary [{:keys [address postcode]}]
  (when (some not-empty [address postcode])
    (str
      (when (not-empty address)
        (str (subs (or address "") 0 (or (str/index-of (or address "") "," 4) 16)) ellipsis " "))
      postcode)))

(defn date-of-birth [{:keys [dob]}]
  (when dob
    (f/unparse (f/formatter "d MMM yyyy") (f/parse (f/formatter "YYYY-MM-dd") dob))))

(defn int-comma [n]
  (cl-format nil "~:d" n))

(def always-uppercase #{"DOB" "NINO"})

(defn -label [s]
  (if (contains? always-uppercase (str/upper-case s))
    (str/upper-case s)
    (->Title-Case s)))

(def label (memoize -label))

(def custom-formatter (f/formatter "EEE d MMM yyyy"))

(def birthday-formatter (f/formatter "d MMM yyyy"))

(def uk-date-format "d/MM/yyyy")

(defn unparse-timestamp [event]
  (if-let [ts (:timestamp event)]
    (assoc event :timestamp (f/unparse custom-formatter ts))
    event))

(defn contact-date [date-time]
  (f/unparse custom-formatter (f/parse uk-date-format date-time  ))
  )

(defn birth-date [date-time]
  (f/unparse birthday-formatter date-time)
  )
