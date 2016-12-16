(ns visualise.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ajax.core :refer [GET]]
            [cljs-time.core :as time]
            [cljs-time.format :as format]))

(defonce !state (reagent/atom {:result         {}
                               :selected-event nil}))


(defn raw-events []
  (map :_source (:hits (:hits (:result @!state)))))

(defn parse-timestamp [timestamp]
  (format/parse (:date-hour-minute-second-ms format/formatters) timestamp))

(defn times []
  (map #(parse-timestamp (:timestamp %)) (raw-events)))

(defn simple [date]
  (format/unparse (format/formatter "dd/MM/yyyy") date))

(defn earliest-day []
  (time/earliest (times)))

(defn latest-day []
  (time/latest (times)))

(defn days-in-resultset []
  (time/in-days (time/interval (earliest-day) (latest-day))))

(defn offset-day [{timestamp :timestamp}]
  (time/in-days (time/interval (earliest-day) (parse-timestamp timestamp))))

(defn event-source-types []
  (into (sorted-set) (map (fn [event] [(:event-source event) (:event-type event)]) (raw-events))))

(defn perform-query [search-term]
  (let [query-string (str "http://192.168.99.100:9200/_search?size=25&q=" search-term)]
    (swap! !state assoc :result {})
    (GET query-string
         {:format          :json
          :response-format :json
          :keywords?       true
          :handler         (fn [response]
                             (swap! !state assoc :result response)
                             )})))

(defn query-box []
  (let [!local (reagent/atom "")]
    (fn []
      [:div.query-box
       [:input {:type        "text"
                :placeholder "enter your search here..."
                :value       @!local
                :on-change   #(reset! !local (-> % .-target .-value))
                :on-key-up   #(if (= "Enter" (-> % .-key)) (perform-query @!local))}]])))

(defn people []
    (map (fn [event] (select-keys event [:name :dob :address])) (raw-events)))

(defn people-selector []
  (fn []
    (let [people {}]
      [:select
       (doall (map (fn [person] [:option {:value  person} (:name person)]) people))])))

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
            [:p "on:" (simple (parse-timestamp (:timestamp event)))]
            ]])))))

(defn result-pic [days-to-show events]
  [:svg.result
   {:view-box (str "0 0 " (+ 14 days-to-show) " 30")
    :height   "100%"
    :width    "100%"}
   [:g {:stroke-width "2px" :stroke "black"}
    [:line {:x1 "0" :y1 "15" :x2 "732" :y2 "15"}]]
   (doall (map (fn [event]
                 ^{:key (gensym)}
                 [:g {:stroke-width "2px" :stroke "blue" :fill "white"}
                  [:circle {:on-mouse-enter (fn [jse]
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
                            :cx             (+ 7 (offset-day event)) :cy "15" :r "5"}]]) events))])

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

(defn home-page []
  [:div
   [query-box]
   ;[people-selector]
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
