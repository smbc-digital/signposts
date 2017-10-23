(ns gov.stockport.sonar.visualise.ui.results.contacts
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]
            [gov.stockport.sonar.visualise.util.date :as d]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.ui.contact-templates.template-map :as tmpl-map]
            [gov.stockport.sonar.visualise.data.colours :as co]
            [gov.stockport.sonar.visualise.util.date :as d]
            ))

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

(defn title-case[word]
  (-> word
      (str/lower-case)
      (str/replace  #"\b." #(.toUpperCase %1))
      ))

(defn sortit [!sort list]
  (let [sorted (sort-by (:sort-func @!sort) list)]
    (if (:ascending @!sort) sorted (reverse sorted))))


(defn event-details [event]
  [:div.event-details-contact
   ((tmpl-map/get-template event) event)
  ])

(defn sort-event-by-timestamp[events-list]
  (sort-by #(- 0 (d/as-millis (:timestamp %))) events-list))

(defn list-events[events]
  (r/with-let [expanded? (r/atom false)]
   (let [events-list (sort-event-by-timestamp(:data events))]
     [:div.container-fluid
      [:div.events-list
      (if (true? @expanded?)
        (map event-details events-list)
        (map event-details (take 2 events-list)))
        ]
      (if (> (count events-list) 2)
       [:div.toggle-data
        (if (true? @expanded?)
        [:p  "SHOW LESS DATA" [:br]
       [:i.fa.fa-arrow-circle-up
        {:on-click #(swap! expanded? not)}
        ]]
        [:p  "SHOW MORE DATA" [:br]
       [:i.fa.fa-arrow-circle-down {:on-click #(swap! expanded? not)}
        ]])])])))

(defn list-people [people]
  (map (fn [[person-key  events]]
         (if (:highlighted? events)
         [:div.person-container
          {:class (cljs.core/name (:color events))}
         [:div.person
          [:div.events-header
         [:h3  (title-case(:name person-key))]
             [:p [:strong(count (:data events))] " contact data listed matches your search criteria"]]
            [list-events events]]]))
       (filter (fn  [[person-key  events]] (:highlighted? events)) people)))

(defn contact-history [!data]
  (let [people (:people @!data)]
    (let [people-list (list-people people)]
    (if  (> (count people-list)  0)
    [:div.contact-history.container-fluid
     [:h4 "All Contact Data"]
        people-list
     ]))))