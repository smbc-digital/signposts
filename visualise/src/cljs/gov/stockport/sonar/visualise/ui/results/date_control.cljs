(ns gov.stockport.sonar.visualise.ui.results.date-control
  (:require [reagent.core :as r]
            [cljs-time.format :as tf]
            [clojure.string :as str]))

(def initial-state {:from-date [] :to-date []})

(defn convert-time-str-to-obj [time-as-str]
  (cljs-time.core/date-time
    (js/parseInt (apply str (subvec (str/split time-as-str "") 0 4)))
    (js/parseInt (apply str (subvec (str/split time-as-str "") 5 6)))
    (js/parseInt (apply str (subvec (str/split time-as-str "") 9 10)))
    ))


(defn start-selector [!data]
  (let [!local (r/atom initial-state)]
    [:div.form.left-date-selector
     "Select start date"
     [:input {:name      "start date"
              :type      "datetime-local"
              :on-change #(swap! !local assoc :from-date (-> % .-target .-value))}]
     [:button.btn.btn-primary
      {:on-click
       #(swap! !data (fn [data] (assoc-in data [:timespan :from-date] (convert-time-str-to-obj (:from-date @!local)))))}]]))


(defn end-selector [!data]
  (let [!local (r/atom initial-state)]
    [:div.form.right-date-selector
     "Select end date"
     [:input {:name "start date"
              :type "datetime-local"
              :on-change
                    #(swap! !local assoc :to-date (-> % .-target .-value))}]
     [:button.btn.btn-primary
      {:on-click
       #(swap! !data (fn [data] (assoc-in data [:timespan :to-date] (convert-time-str-to-obj (:to-date @!local)))))}]]))



