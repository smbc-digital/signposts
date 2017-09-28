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

(defn event-title [[k v]]
  [:div (fh/label (name k)) ])

(defn event-value [[k v]]
  (if (str/blank? v) [:div "-"] [:div v] ))

(defn show-event-titles-in-column [events]
  [:div.col-2 (map event-title events) ])

(defn show-event-values-in-column [events]
  [:div.col-2 (map event-value events) ])

(defn third-of [events]
  (int (/ (count events) 3)))

(defn two-thirds-of [events]
  (- (count events) (third-of events)))

(defn count-middle-third [events]
  (int (/ (two-thirds-of events) 2)))

(defn count-last-third [events]
  (int (- (two-thirds-of events) (count-middle-third events))))

(defn first-third [events]
  (take (third-of events) events))

(defn middle-third [events]
  (take (count-middle-third events) (reverse (take (two-thirds-of events) (reverse events)))))

(defn last-third [events]
  (reverse (take (count-last-third events) (reverse events))))

(defn event-details [event]
  (let [selected-events  (selected-kvs event)]
  [:div {:style {:border-top "solid 1px #ccc" :margin "15px 5px 10px 5px" :padding "10px 5px 10px 5px"}}
  [:h4  (:event-source event) " " [:span {:style {:font-weight "normal"}} (:event-type event)]]
   [:div.container-fluid
    [:div.row.no-gutters
     (show-event-titles-in-column (first-third selected-events))
     (show-event-values-in-column (first-third selected-events))
     (show-event-titles-in-column (middle-third selected-events))
     (show-event-values-in-column (middle-third selected-events))
     (show-event-titles-in-column (last-third selected-events))
     (show-event-values-in-column (last-third selected-events))
  ]]
   ]))


(defn list-events[events]
      (r/with-let [expanded? (r/atom false)]
                  (let [events-list  (:data events)]
     [:div.container-fluid
      (if (true? @expanded?)
        (map event-details events-list)
        (map event-details (take 4 events-list))
        )
      (if (> (count events-list) 4)
       [:p {:style {:text-align "center" :font-size "2em"}}
        (if (true? @expanded?)
       [:i.fa.fa-arrow-circle-up
        {:on-click #(swap! expanded? not)}
        ]
       [:i.fa.fa-arrow-circle-down
        {:on-click #(swap! expanded? not)}
        ])])])))

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