(ns gov.stockport.sonar.visualise.ui.results.contacts
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]
            [gov.stockport.sonar.visualise.util.date :as d]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.ui.templates.template-map :as tmpl-map]
            [gov.stockport.sonar.visualise.data.colours :as co]
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
  [:div {:style {:border-bottom "solid 1px #ccc" :margin "15px 0px 10px 0px" :padding "10px 5px 10px 5px"}}
   ((tmpl-map/get-template event) event)
  ])


(defn list-events[events]
      (r/with-let [expanded? (r/atom false)]
                  (let [events-list  (:data events)]
     [:div.container-fluid
      (if (true? @expanded?)
        (map event-details events-list)
        (map event-details (take 4 events-list))
        )
      (if (> (count events-list) 4)
       [:div {:style {:text-align "center"}}
        (if (true? @expanded?)
        [:p {:style {:font-size "0.9em" :color "#468CC8" :font-weight 600}} "SHOW LESS DATA" [:br]
       [:i.fa.fa-arrow-circle-up {:style {:font-size "2em" :color "#468CC8"}
        :on-click #(swap! expanded? not)}
        ]]
        [:p {:style {:font-size "0.9em" :color "#468CC8" :font-weight 600}} "SHOW MORE DATA" [:br]
       [:i.fa.fa-arrow-circle-down {:style {:font-size "2em" :color "#468CC8"}
        :on-click #(swap! expanded? not)}
        ]])])])))

(defn list-people [people]
  (map (fn [[person-key  events]]
         (if (:highlighted? events)
         [:div {:class (str "left person")
             :style
             {:box-shadow "10px 10px 10px #ccc"
              :margin "10px 0px 20px 10px"
              :border-top "1px solid #ccc"
              :border-left (str "5px solid " ((:color events) co/colour-map "#ccc"))
              :border-radius "5px"
              :padding "20px 0px 5px 0px"
              }}
          [:div
         [:h3 {:style {:text-align "center" :color "#486573"}} (title-case(:name person-key))]
             [:p {:style {:text-align "center" :color "#486573"}}[:strong(count (:data events))] " contact data listed matches your search criteria"]
            [list-events events]]]))
           people))

(defn contact-history [!data]
  (let [results (:people @!data)]
    (if (>  (count results) 0)
    [:div.contact-history.container-fluid
     [:h4 "All contact data"]
     (list-people results)])))