(ns gov.stockport.sonar.visualise.ui.results.contacts
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]
            [gov.stockport.sonar.visualise.util.date :as d]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.data.people :as people]))

(def dob-unformatter (f/formatter "yyyy-mm-dd"))
(def dob-formatter (f/formatter "dd MMM yyyy"))

(def standard-keys [:event-source :event-type :address :postcode :timestamp])

(def custom-formatter (f/formatter "dd MMM yyyy HH:mm:ss"))

(defn ts [ts]
  (f/unparse (:date f/formatters) ts))

(defn unparse-timestamp [event]
  (if-let [ts (:timestamp event)]
    (assoc event :timestamp (f/unparse custom-formatter ts))
    event))

(defn unparse-dob [event]
  (if-let [ts (:dob event)]
    (assoc event :dob (->> ts (f/parse dob-unformatter) (f/unparse dob-formatter)))
    event))

(def surname #(last (str/split (:name %) #" ")))

(defn sortit [!sort list]
  (let [sorted (sort-by (:sort-func @!sort) list)]
    (if (:ascending @!sort) sorted (reverse sorted))))

(defn selected-kvs [event]
  (let [event-with-formatted-timestamp (-> event unparse-timestamp unparse-dob)
        other-keys (sort (keys (apply dissoc (dissoc event :id :ingestion-timestamp :score :name :dob) standard-keys)))]
    (map
      (fn [k] [k (get event-with-formatted-timestamp k "")])
      (concat standard-keys other-keys))))

(defn row [[k v]]
  [:tr [:th (fh/label (name k))] [:td v]])

(defn rows [event]
  (map row (selected-kvs event)))

(defn contact-history [!data]
  (let [!sort-fn (r/atom {:sort-func :name
                          :ascending true})]
    (fn []
      (let [results (:people @!data)]
        (if (not-empty results)
          [:div
          [:h3 (:name (first results) ) " (DOB: " (:dob (unparse-dob (first results) )) ")"]
            [:table
            (map
              (fn [event]
                  [:thead [:tr [:th (get :event-source event)][:td]] ]
                  [:tbody
                   (rows event)
                  ]
                   )
              (sortit !sort-fn (people/all-events @!data)))]])))))