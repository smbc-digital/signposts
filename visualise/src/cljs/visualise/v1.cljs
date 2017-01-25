(ns visualise.v1
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [cljs-time.format :as format]
            [goog.crypt.base64 :as b64]
            [visualise.ui.explore :refer [graph view-state]]
            ))

(defonce !creds (reagent/atom {}))
(defonce !state (reagent/atom {:result         {} :selected-event nil}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (b64/encodeString (str (:username @!creds) ":" (:password @!creds))))})

(defn parse-timestamp [timestamp]
  (format/parse (:date-time format/formatters) timestamp))

(defn raw-events []
  (:result @!state))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map :_source (-> response :hits :hits))))

(defn perform-query [search-term]
  (let [query-string (str "http://192.168.99.100:9200/events-*/_search?size=250&q=" search-term)]
    (swap! !state assoc :result {})
    (GET query-string
         {:headers         (authorisation-header)
          :format          :json
          :response-format :json
          :keywords?       true
          :handler         (fn [response]
                             (swap! !state #(-> %
                                                (assoc :total (-> response :hits :total))
                                                (assoc :took-millis (-> response :took))
                                                (assoc :result (source-events response)))))})))

(defn pluralise
  ([value singular] (str value " " singular (when-not (= 1 value) "s")))
  ([value singular plural] (str value " " (if (= 1 value) singular plural))))

(defn people []
  (into (sorted-set)
        (map (fn [event]
               (into [] (vals (select-keys event [:name :dob :address])))) (raw-events))))

(defn query-box [!local]
  (fn []
    [:div.query-box
     [:input {:type        "text"
              :placeholder "enter your search here..."
              :value       @!local
              :on-change   #(reset! !local (-> % .-target .-value))
              :on-key-up   #(if (= "Enter" (-> % .-key)) (perform-query @!local))}]]))

(defn people-selector [!local]
  (fn []
    (if (not (empty? (raw-events)))
      (let [people (people)
            display (fn [[name dob address]] (str name "- " dob " - " address))]
        [:div
         [:p "Your search took " (:took-millis @!state) "ms to find " [:strong (pluralise (:total @!state) "event")]]
         [:p "Displaying the best " [:strong (pluralise (count (raw-events)) "event")] ", involving " [:strong (pluralise (count people) "person" "people")]]
         (if (= 1 (count people))
           [:p [:strong "Person: "] (display (first people))]
           [:p [:strong "People: "] [:select {:on-change (fn [event] (perform-query (reset! !local (-> event .-target .-value))))}
                                     (doall (map (fn [[name dob :as person]]
                                                   ^{:key person}
                                                   [:option {:value (str "name:" name " AND dob:" dob)} (display person)]) people))]])]))))

(defn query-area []
  (let [!qstate (reagent/atom "")]
    (fn []
      [:div
       [query-box !qstate]
       [people-selector !qstate]])))

(defn results []
  (fn []
    [:div
     (let [events (raw-events)]
       (if (not-empty events)
         [graph (view-state {:vb-h 300 :resolution :quarters}) events]))]))

(defn creds-area []
  (fn []
    [:div
     [:input {:type        "text"
              :placeholder "username"
              :value       (or (:username @!creds) "")
              :on-change   #(swap! !creds assoc :username (-> % .-target .-value))}]
     [:input {:type        "password"
              :placeholder "password"
              :value       (or (:password @!creds) "")
              :on-change   #(swap! !creds assoc :password (-> % .-target .-value))}]
     ]))

(defn home-page []
  [:div
   [creds-area]
   [query-area]
   [results]
   ])