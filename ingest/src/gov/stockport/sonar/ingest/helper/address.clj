(ns gov.stockport.sonar.ingest.helper.address
  (:require [camel-snake-kebab.core :refer [convert-case]]
            [clojure.string :as str]))

(defn- capitalise-long-terms [s]
  (if (< (count s) 3) s (str/capitalize s)))

(def ->Title-Case (partial convert-case capitalise-long-terms capitalise-long-terms " "))

(def uk-address-regex #"(.*?)([ ]*?)((GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2}))([ ]*)(.*)")

(defn- address-parts [val]
  (when (some? val)
    (let [[_ preamble pre-whitespace postcode :as full-result] (re-find uk-address-regex val)
          [postamble post-whitespace] (reverse full-result)]
      (if full-result
        {:preamble        preamble
         :pre-whitespace  pre-whitespace
         :postcode        postcode
         :post-whitespace post-whitespace
         :postamble       postamble}
        {:preamble val}))))

(defn postcode [val]
  (:postcode (address-parts val)))

(defn ->title-case-address [val]
  (when (some? val)
    (let [{:keys [preamble pre-whitespace postcode post-whitespace postamble]} (address-parts val)]
      (str (->Title-Case preamble) pre-whitespace postcode post-whitespace (->Title-Case postamble)))))