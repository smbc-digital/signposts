(ns gov.stockport.sonar.visualise.util.fmt-help
  (:require [clojure.string :as str]
            [cljs-time.format :as f]))

(def ellipsis \u2026)

(defn address-summary [{:keys [address postcode]}]
  (when (some not-empty [address postcode])
    (str
      (when (not-empty address)
        (str (subs (or address "") 0 (or (str/index-of (or address "") ",") 16)) ellipsis " "))
      postcode)))

(defn date-of-birth [{:keys [dob]}]
  (when dob
    (f/unparse (f/formatter "d MMM yyyy") (f/parse (f/formatter "YYYY-MM-dd") dob))))


