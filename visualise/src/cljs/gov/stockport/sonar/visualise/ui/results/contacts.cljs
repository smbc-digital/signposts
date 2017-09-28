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

(def standard-keys [:address :postcode :timestamp])

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
        other-keys (sort (keys (apply dissoc (dissoc event :id :ingestion-timestamp :score :name :event-type :event-source ) standard-keys)))]
    (map
      (fn [k] [k (get event-with-formatted-timestamp k "")])
      (concat standard-keys other-keys))))

(defn table-row [[k v]]
  [:tr [:th (fh/label (name k))] [:td v]])

(defn event-details [event]
  [:div {:style {:border-top "solid 1px #ccc" :margin "15px 5px 10px 5px" :padding "10px 5px 10px 5px"}}
  [:h4  (:event-source event) " " [:span {:style {:font-weight "normal"}} (:event-type event)]]
  [:table
   [:tbody
     (map table-row (selected-kvs event))
  ]]])

(defn toggle-events[events]
  (if (:display-all events)
    (dissoc events :display-all)
    (assoc events :display-all true)
    )
  )

(defn list-events[events]
    (let [events-list  (:data events)]
      (let [display-all! (r/atom false)]
     (fn[]
     [:div.container-fluid
      (if (= display-all! true)
        (map event-details events-list)
        (map event-details (take 4 events-list))
        )
      (if (> (count events-list) 4)
      [:p {:style {:text-align "center" :font-size "2em"}}
       [:i.fa.fa-arrow-circle-down
        {:on-click (fn[e] (swap! display-all! true))}
        ]]
      )]
     ))))

(defn list-people [people]
  (map (fn [[person-key  events]]
         (if (:highlighted? events)
         [:div {:class (:color events)}
         [:h3 {:style {:text-align "center"}} (:name person-key) ]
             [:p {:style {:text-align "center"}}[:strong(count (:data events))] " contact data listed matches your search criteria"]
            [list-events events]]))
           people))

(defn contact-history [!data]
  (let [results (:people @!data)]
    [:div(list-people results)]
    )
  )