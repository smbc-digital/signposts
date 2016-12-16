(ns visualise.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ajax.core :refer [GET]]
            [cljs-time.core :as time]
            [cljs-time.format :as format]))


(def example {:took 23, :timed_out false, :_shards {:total 16, :successful 16, :failed 0}, :hits {:total 68, :max_score 5.1923194, :hits [{:_index "feed_schools", :_type "exclusion", :_id "AVkCYlq0HKc4AMKM8A2F", :_score 5.1923194, :_source {:name "Lizzie Altenwerth", :dob "2000-06-05", :address "66157 Hayes Stream,Powys,BM61 4DE", :timestamp "2016-11-14T12:08:37.156Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_gmp", :_type "asbo", :_id "AVkCYka2HKc4AMKM8Alk", :_score 4.732541, :_source {:name "Lizzie Altenwerth", :dob "2001-03-21", :address "6643 Pfeffer Stravenue,Central,BT1 8RD", :timestamp "2016-05-08T12:08:32.041Z", :event-source "GMP", :event-type "ASBO"}} {:_index "feed_gmp", :_type "caution", :_id "AVkCYmi7HKc4AMKM8BB4", :_score 4.732541, :_source {:name "Lizzie Altenwerth", :dob "1997-10-01", :address "6315 O'Keefe Manor,Somerset,TL53 3KZ", :timestamp "2016-03-31T12:08:40.750Z", :event-source "GMP", :event-type "CAUTION"}} {:_index "feed_schools", :_type "exclusion", :_id "AVkCYlD6HKc4AMKM8Avl", :_score 4.5698967, :_source {:name "Lizzie Altenwerth", :dob "2002-09-21", :address "965 Hane Mall,Kent,RV9 6KW", :timestamp "2015-08-23T12:08:34.669Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_schools", :_type "exclusion", :_id "AVkCYmi9HKc4AMKM8BB5", :_score 4.5698967, :_source {:name "Lizzie Altenwerth", :dob "1997-10-01", :address "6315 O'Keefe Manor,Somerset,TL53 3KZ", :timestamp "2015-06-23T12:08:40.752Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_schools", :_type "exclusion", :_id "AVkCYlq4HKc4AMKM8A2G", :_score 4.349698, :_source {:name "Lizzie Altenwerth", :dob "2000-06-05", :address "66157 Hayes Stream,Powys,BM61 4DE", :timestamp "2016-01-03T12:08:37.162Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_schools", :_type "exclusion", :_id "AVkCYlrWHKc4AMKM8A2M", :_score 4.349698, :_source {:name "Lizzie Altenwerth", :dob "2000-06-05", :address "66157 Hayes Stream,Powys,BM61 4DE", :timestamp "2016-03-04T12:08:37.193Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_schools", :_type "exclusion", :_id "AVkCYmqqHKc4AMKM8BEX", :_score 4.349698, :_source {:name "Lizzie Altenwerth", :dob "2000-02-15", :address "212 Dickinson Stream,Oxfordshire,ST86 4NJ", :timestamp "2015-08-20T12:08:41.245Z", :event-source "SCHOOLS", :event-type "EXCLUSION"}} {:_index "feed_homes", :_type "arrears", :_id "AVkCYimyHKc4AMKM8ANv", :_score 4.058346, :_source {:name "Lizzie Altenwerth", :dob "2000-04-11", :address "557 Oran Ferry,Lothian,RA34 9GA", :timestamp "2015-12-10T12:08:24.612Z", :event-source "HOMES", :event-type "ARREARS"}} {:_index "feed_homes", :_type "eviction", :_id "AVkCYmYKHKc4AMKM8A_H", :_score 4.058346, :_source {:name "Lizzie Altenwerth", :dob "1998-03-14", :address "1316 Wayne Extensions,Herefordshire,XM84 1VZ", :timestamp "2016-04-29T12:08:40.061Z", :event-source "HOMES", :event-type "EVICTION"}}]}})


(defonce !state (reagent/atom {:result         example
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
  (let [query-string (str "http://192.168.99.100:9200/_search?size=50&q=" search-term)]
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
     [:input {:type      "text"
              :value     @!local
              :on-change #(reset! !local (-> % .-target .-value))}]
     [:button {:on-click #(perform-query @!local)} "Search"]])))

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
