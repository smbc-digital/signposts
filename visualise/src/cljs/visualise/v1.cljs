(ns visualise.v1
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [cljs-time.format :as f]
            [goog.crypt.base64 :as b64]
            [visualise.ui.explore :refer [graph view-state]]
            [visualise.ui.records :refer [record-list]]
            [visualise.query.client :refer [query ->json]]
            [visualise.common.query.base :as qb]
            [visualise.util.date :as d]
            [visualise.ui.facet :refer [->cs facet-tree]]
            [cljs-time.core :as t]))

(defonce !creds (reagent/atom {:username "elastic" :password "changeme"}))

(defonce !state (reagent/atom {:result {} :selected-event nil}))


(defn raw-events []
  (:result @!state))

(defn parse-timestamp [timestamp]
  (f/parse (:date-time f/formatters) timestamp))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map :_source (-> response :hits :hits))))

(def query-handler
  (fn [response]
    (swap! !state #(-> %
                       (assoc :total (-> response :hits :total))
                       (assoc :took-millis (-> response :took))
                       (assoc :result (source-events response))))))

(defn perform-query [{:keys [query-string max-age]}]
  (let [query-map (-> (qb/query)
                      (qb/with-size 250)
                      (qb/with-max-age max-age)
                      (qb/with-query-string query-string))]
    (println (->json query-map))
    (query "/events-*/_search" query-map query-handler)))

(defn pluralise
  ([value singular] (str value " " singular (when-not (= 1 value) "s")))
  ([value singular plural] (str value " " (if (= 1 value) singular plural))))

(defn people [result]
  (into (sorted-set)
        (map (fn [event]
               (into [] (vals (select-keys event [:name :dob])))) result)))

(defn addresses [result]
  (into (sorted-set)
        (map (fn [event]
               (into [] (vals (select-keys event [:address])))) result)))

(defn drop-down [name rows]
  [:div.drop-down
   [:label [:strong name]
    [:select
     (doall (map (fn [row] ^{:key (gensym)} [:option row]) (sort rows)))]]])


(defn people-display [!state]
  (fn []
    (let [results (:result @!state)]
      (if (not-empty results)
        (let [people (people results)
              rows (map (fn [[name dob]] (str name " - " dob " - aged " (d/age dob) " years")) people)]
          [drop-down "People" rows])))))

(defn address-display [!state]
  (fn []
    (let [results (:result @!state)]
      (if (not-empty results)
        (let [addresses (addresses results)
              rows (map (fn [[address]] (str address)) addresses)]
          [drop-down "Addresses" rows])))))

(defn query-on-enter [!local] #(if (= "Enter" (-> % .-key)) (perform-query @!local)))

(defn query-box [!local]
  (fn []
    [:div.query-box
     [:div.form-group
      [:input.col-12.form-control
       {:type        "text"
        :placeholder "enter your search here..."
        :value       (:query-string @!local)
        :on-change   #(swap! !local assoc :query-string (-> % .-target .-value))
        :on-key-up   (query-on-enter !local)}]]
     [:div.form-group
      [:label.col-2.col-form-label "aged up to"]
      [:input.col-10.form-control {:type      "text"
                                   :value     (:max-age @!local)
                                   :on-change #(swap! !local assoc :max-age (-> % .-target .-value))
                                   :on-key-up (query-on-enter !local)}]]
     ;[:label "From: "
     ; [:input.from {:type      :date
     ;               :value     (:from @!local)
     ;               :on-change #(swap! !local assoc :from (-> % .-target .-value))}]]
     ;[:label "To: "
     ; [:input.to {:type      :date
     ;             :value     (:to @!local)
     ;             :on-change #(swap! !local assoc :to (-> % .-target .-value))}]]
     ]))

(defn people-selector [!local]
  (fn []
    (if (not (empty? (raw-events)))
      (let [people (people (raw-events))
            display (fn [[name dob]] (str name " - " dob " - aged " (d/age dob) " years"))]
        [:div
         [:p "Your search took " (:took-millis @!state) "ms to find " [:strong (pluralise (:total @!state) "event")]]
         [:p "Displaying the best " [:strong (pluralise (count (raw-events)) "event")] ", involving " [:strong (pluralise (count people) "person" "people")]]
         (if (= 1 (count people))
           [:p [:strong "Person: "] (display (first people))]
           [:p [:strong "People: "] [:select {:size 2 :on-change (fn [event] (perform-query (reset! !local (-> event .-target .-value))))}
                                     (doall (map (fn [[name dob :as person]]
                                                   ^{:key person}
                                                   [:option {:value (str "name:" name " AND dob:" dob)} (display person)]) people))]])]))))

(defn query-area []
  (let [!qstate (reagent/atom {:query-string ""
                               :from         "2010-01-01"
                               :to           (f/unparse (f/formatter "yyyy-MM-dd") (t/now))
                               :max-age      99})]
    (fn []
      [:div.col-sm-4
       [:div.panel.panel-primary
        [:div.panel-heading
         [:div.panel-title "Bootstrap Panel..."]]
        [:div.panel-body
         [query-box !qstate]
         ;[people-selector !qstate]
         ]]])))

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
  [:div.container
   ;[creds-area]
   [query-area]
   [people-display !state]
   [address-display !state]
   [results]
   [record-list !state]
   ])