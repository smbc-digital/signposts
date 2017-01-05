(ns visualise.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ajax.core :refer [GET]]
            [cljs-time.core :as time]
            [cljs-time.format :as format]
            [goog.crypt.base64 :as b64]))

(defonce !creds (reagent/atom {}))
(defonce !state (reagent/atom {:result         {}
                               :selected-event nil}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (b64/encodeString (str (:username @!creds) ":" (:password @!creds))))})

(defn parse-timestamp [timestamp]
  (format/parse (:date-hour-minute-second-ms format/formatters) timestamp))

(defn simple [date]
  (format/unparse (format/formatter "dd/MM/yyyy") date))

(defn raw-events []
  (:result @!state))

(defn days-in-resultset []
  (:diff @!state))

(defn source-events [response]
  (map #(-> %
            (update :timestamp parse-timestamp))
       (map :_source (-> response :hits :hits))))

(defn add-date-info [{:keys [result] :as state}]
  (let [earliest (time/earliest (map :timestamp result))
        latest (time/latest (map :timestamp result))
        diff (time/in-days (time/interval earliest latest))]
    (assoc state
      :earliest earliest
      :latest latest
      :diff diff)))

(defn add-offsets [{:keys [result earliest] :as state}]
  (assoc state :result
               (map
                 (fn [event]
                   (assoc event :offset (time/in-days (time/interval earliest (:timestamp event)))))
                 result)))

(defn perform-query [search-term]
  (let [query-string (str "http://192.168.99.100:9200/_search?size=50&q=" search-term)]
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
                                                (assoc :result (source-events response))
                                                (add-date-info)
                                                (add-offsets))))})))

(defn event-source-types []
  (into (sorted-set) (map (fn [event] [(:event-source event) (:event-type event)]) (raw-events))))

(defn pluralise
  ([value singular] (str value " " singular (when-not (= 1 value) "s")))
  ([value singular plural] (str value " " (if (= 1 value) singular plural))))


(defn people []
  (into (sorted-set) (map (fn [event] (into [] (vals (select-keys event [:name :dob :address])))) (raw-events))))


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

(defn selected-event-popup []
  (fn []
    (let [{:keys [x y sx sy] :as event} (:selected-event @!state)]
      (if (not (nil? event))
        ;(let [x (if (> sx 500) (- x 400) x)
        ;      y (if (> sy 500) (- y 200) y)]
        (doall
          [:div.popup-parent
           {:style {:top (+ 30 y) :left (+ 30 x)}}
           [:div.popup
            [:p "who: " (:name event)]
            [:p "dob: " (:dob event)]
            [:p "on:" (simple (:timestamp event))]
            ]])))))

(defn result-pic [days-to-show events]
  [:svg.result
   {:view-box (str "0 0 " (+ 14 days-to-show) " 30")
    :height   "100%"
    :width    "100%"}
   [:g {:stroke-width "2px" :stroke "black"}
    [:line {:x1 "0" :y1 "15" :x2 (+ 14 days-to-show) :y2 "15"}]]
   (doall (map (fn [event]
                 ^{:key (gensym)}
                 [:g
                  [:text {:y 30 :text-anchor :middle :x (+ 7 (:offset event)) :font-size "0.3em"} (simple (:timestamp event))]
                  [:circle {:stroke-width   "2px" :stroke "blue" :fill "white"
                            :on-mouse-enter (fn [jse]
                                              (swap! !state
                                                     (fn [state]
                                                       (assoc state :selected-event
                                                                    (-> event
                                                                        (assoc :x (-> jse .-clientX))
                                                                        (assoc :y (-> jse .-clientY))
                                                                        (assoc :sx (-> jse .-screenY))
                                                                        (assoc :sy (-> jse .-screenY))
                                                                        )))))
                            :on-mouse-leave #(swap! !state assoc :selected-event nil)
                            :cx             (+ 7 (:offset event)) :cy "15" :r "5"}]]) events))])

(defn result [[event-source event-type]]
  (let [events (filter #(and (= event-source (:event-source %)) (= event-type (:event-type %))) (raw-events))
        days-to-show (days-in-resultset)]
    ^{:key (gensym)}
    [:div
     [:p (str event-source " - " event-type " [" (count events) "]")]
     [result-pic days-to-show events]
     ]))

(defn results []
  (fn []
    [:div
     (doall (map result (event-source-types)))]))

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
   [selected-event-popup]
   [results]
   ])

(defn about-page []
  [:div [:h2 "About visualise"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
                    (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
                    (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
