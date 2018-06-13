(ns gov.stockport.sonar.visualise.util.fmt-help
  (:require [clojure.string :as str]
            [cljs-time.format :as f]
            [cljs.pprint :refer [cl-format]]
            [camel-snake-kebab.core :refer [convert-case]]
            [clojure.string :as s]))

(def ellipsis \u2026)

(def ->Title-Case (partial convert-case clojure.string/capitalize clojure.string/capitalize " "))

(defn title-case[word]
  (-> word
      (str/lower-case)
      (str/replace  #"\b." #(.toUpperCase %1))
      ))

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

(def always-uppercase #{"DOB" "NINO" "CIN" "LAC" "ASBO" "ASB" "SEN" "AWOL"})

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


(def dob-unformatter (f/formatter "yyyy-MM-dd"))
(def dob-formatter (f/formatter "d MMM yyyy"))
(def si-unformatter (f/formatter "yyyy-MM-ddThh:mm:ssz"))
(def uk-unformatter(f/formatter uk-date-format))


(defn to-dob [date]
  (if (s/blank? date)
    ""
  (f/unparse dob-formatter(f/parse dob-unformatter date))))


(defn close-date[date-time]
  (f/unparse custom-formatter (f/parse dob-unformatter  date-time)))

(defn eis-close-date[date-time]
  (f/unparse custom-formatter (f/parse si-unformatter  date-time)))

(defn cf-close-date[date-time]
  (f/unparse custom-formatter (f/parse uk-unformatter  date-time)))

(defn unparse-dob [event]
  (if-let [ts (:dob event)]
    (assoc event :dob (->> ts (f/parse dob-unformatter) (f/unparse dob-formatter)))
    event))